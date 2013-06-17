/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package net.xclinical.iso11179.ext;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.DataElement;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.DesignationContext;
import net.xclinical.iso11179.Item;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Namespace;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.ScopedIdentifier;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;
import net.xclinical.odm.BasicDefinitions;
import net.xclinical.odm.CodeList;
import net.xclinical.odm.CodeListItem;
import net.xclinical.odm.CodeListRef;
import net.xclinical.odm.DataType;
import net.xclinical.odm.Document;
import net.xclinical.odm.ItemDef;
import net.xclinical.odm.MeasurementUnit;
import net.xclinical.odm.MeasurementUnitRef;
import net.xclinical.odm.MetaDataVersion;
import net.xclinical.odm.OID;
import net.xclinical.odm.Study;
import net.xclinical.odm.TranslatedText;
import net.xclinical.odm.xml.ODMExporter;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

/**
 * This is bound to ODM at this time!
 * 
 * @author ms@xclinical.com
 */
public final class ExportDocumentCommand extends AbstractCommand {

	Item item;
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		item = TreeBinding.bind(source.subNode("item"));

		if (item == null) throw new RemoteException("No item specified");
		if (!(item instanceof Context)) throw new RemoteException("Specified element is not a context");
		
		Document document = new Document(); 
		Study study = new Study();
		Designation d = item.getFirstDesignation();
		if (d != null) {
			study.getGlobalVariables().setStudyName(d.getSign());
		}
		
		document.addStudy(study);
		MetaDataVersion metaDataVersion = new MetaDataVersion();
		study.add(metaDataVersion);
		
		Context context = (Context)item;
		parse(study, metaDataVersion, context);
				
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		try {
			ODMExporter exporter = ODMExporter.newExporter(buffer);
			exporter.writeDocument(document);
			
			final Context root = Context.root();
			
			net.xclinical.iso11179.ext.Document export = net.xclinical.iso11179.ext.Document.create("ODM Document", "application/xml", buffer.toByteArray());
			ReferenceDocument exportedDocument = ReferenceDocument.create("ODM Export", "/mdr/document/" + export.getId());
			LanguageIdentification lang = LanguageIdentification.findOrCreate(LanguageIdentification.EN_US);	
			root.designate(exportedDocument, "ODM Document", lang);
			Namespace ns = Namespace.findOrCreate(Namespace.DATA);
			ScopedIdentifier identifier = ScopedIdentifier.create(ns, export.getKey().toString());
			exportedDocument.addScopedIdentifier(identifier);
			
			FlatJsonExporter.copy(writer, exportedDocument);		
		}
		catch(XMLStreamException e) {
			throw new RemoteException(e);
		}
	}

	private static OID generateOid(Item item) {
		Key itemKey = item.getKey();
		return OID.parse("i." + itemKey.getSimpleName() + "." + itemKey.getValue());
	}
	
	private static void parse(Study study, MetaDataVersion metaDataVersion, Context context) {
		for (DesignationContext relevantDesignation : context.getRelevantDesignations()) {
			Designation d = relevantDesignation.getDesignation();
			Item item = d.getItem();
			if (item instanceof Context) {
				parse(study, metaDataVersion, (Context)item);
			}
			else if (item instanceof DataElement) {
				DataElement dataElement = (DataElement)item;
				
				ItemDef itemDef = new ItemDef();
				itemDef.setOid(generateOid(item));
				
				metaDataVersion.add(itemDef);
				
				ValueDomain domain = dataElement.getDomain();
				if (domain != null) {					
					net.xclinical.iso11179.DataType dataType = domain.getDataType();
					if (dataType != null) {
						ScopedIdentifier scopedIdentifier = dataType.getScopedIdentifier(Namespace.find("http://www.cdisc.org/ns/odm/v1.3"));
						if (scopedIdentifier != null) {
							DataType dt = DataType.fromCode(scopedIdentifier.getIdentifier());
							itemDef.setDataType(dt);
						}
					}
					
					UnitOfMeasure unitOfMeasure = domain.getUnitOfMeasure();
					if (unitOfMeasure != null) {
						OID oid = generateOid(unitOfMeasure);
						BasicDefinitions basicDefinitions = study.getBasicDefinitions();
						MeasurementUnit measurementUnit = basicDefinitions.getMeasurementUnits().get(oid);
						if (measurementUnit == null) {
							measurementUnit = new MeasurementUnit();
							measurementUnit.setOid(oid);
							for (Designation ud : unitOfMeasure.getDesignations()) {
								measurementUnit.getSymbol().add(new TranslatedText(ud.getLanguage().getLanguageIdentifier(), ud.getSign()));
							}
							basicDefinitions.addMeasurementUnit(measurementUnit);						
						}
						itemDef.add(new MeasurementUnitRef(measurementUnit.getOid()));
					}				
					
					Set<PermissibleValue> members = domain.getMembers();
					if (members.size() > 0) {
						OID codeListOid = OID.parse("i.CodeList." + domain.getId());
						CodeList codeList = metaDataVersion.getCodeLists().get(codeListOid);
						if (codeList == null) {
							codeList = new CodeList();
							codeList.setOid(codeListOid);

							for (PermissibleValue permissibleValue : domain.getMembers()) {
								ValueMeaning valueMeaning = permissibleValue.getMeaning();
								final String codedValue = permissibleValue.getPermittedValue();
								
								CodeListItem codeListItem = new CodeListItem();
								codeListItem.setCodedValue(codedValue);
								
								for (Designation ud : valueMeaning.getDesignations()) {
									codeListItem.getDecode().add(new TranslatedText(ud.getLanguage().getLanguageIdentifier(), ud.getSign()));
								}
								
								codeList.add(codeListItem);
							}
							
							metaDataVersion.add(codeList);
						}
						
						itemDef.setCodeList(new CodeListRef(codeListOid));
					}
				}
								
				for (Designation itemDesignation : dataElement.getDesignations()) {
					LanguageIdentification lang = itemDesignation.getLanguage();
					itemDef.getDescriptions().add(new TranslatedText(lang.getLanguageIdentifier(), itemDesignation.getSign()));
				}				
			}
		}		
	}
}
