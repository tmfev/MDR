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
package net.xclinical.odm.xml;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
import net.xclinical.odm.ODM130;
import net.xclinical.odm.OID;
import net.xclinical.odm.Study;
import net.xclinical.odm.TranslatedText;
import net.xclinical.odm.TranslatedTextCollection;

public class ODMExporter {

	private static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
	
	private XMLStreamWriter writer;
	
	private XMLOutputFactory factory;
	
	private ODMExporter(OutputStream destination) throws XMLStreamException {
		factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(destination, "UTF-8");
	}

	
	public static ODMExporter newExporter(OutputStream destination) throws XMLStreamException {
		return new ODMExporter(destination);
	}

	public void writeDocument(Document document) throws XMLStreamException {
		writer.writeStartDocument();
		writer.setDefaultNamespace(ODM130.NAMESPACE);
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.ODM.ELEMENT);
		writer.writeDefaultNamespace(ODM130.NAMESPACE);
		
		for (Study study : document.getStudies()) {
			writeStudy(study);
		}
		
		writer.writeEndElement();		
		writer.writeEndDocument();
		
		writer.flush();
	}
	
	private void writeStudy(Study study) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.Study.ELEMENT);

		writeBasicDefinitions(study.getBasicDefinitions());
		
		for (MetaDataVersion metaDataVersion : study.getMetaDataVersions()) {
			writeMetaDataVersion(metaDataVersion);
		}
		
		writer.writeEndElement();				
	}

	private void writeBasicDefinitions(BasicDefinitions basicDefinitions) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.BasicDefinitions.ELEMENT);
		
		for (MeasurementUnit u : basicDefinitions.getMeasurementUnits().elements()) {
			writeMeasurementUnit(u);
		}
		
		writer.writeEndElement();
	}

	private void writeMeasurementUnit(MeasurementUnit u) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.MeasurementUnit.ELEMENT);

		OID oid = u.getOid();
		if (oid != null) {
			writer.writeAttribute(ODM130.MeasurementUnit.OID, oid.getValue());
		}

		String name = u.getName();
		if (name != null) {
			writer.writeAttribute(ODM130.MeasurementUnit.NAME, name);
		}
		
		writeTranslatedText(u.getSymbol());
		
		writer.writeEndElement();
	}

	private void writeMetaDataVersion(MetaDataVersion metaDataVersion) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.MetaDataVersion.ELEMENT);

		for (ItemDef item : metaDataVersion.getItems().elements()) {
			writeItemDef(item);
		}

		for (CodeList codeList : metaDataVersion.getCodeLists().elements()) {
			writeCodeList(codeList);
		}
		
		writer.writeEndElement();				
	}

	private void writeItemDef(ItemDef itemDef) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.ItemDef.ELEMENT);

		OID oid = itemDef.getOid();
		if (oid != null) {
			writer.writeAttribute(ODM130.ItemDef.OID, oid.getValue());
		}
		
		DataType dataType = itemDef.getDataType();
		if (dataType != null) {
			writer.writeAttribute(ODM130.ItemDef.DATA_TYPE, dataType.getCode());
		}
		
		for (MeasurementUnitRef measurementUnitRef : itemDef.getMeasurementUnits()) {
			writer.writeStartElement(ODM130.NAMESPACE, ODM130.MeasurementUnitRef.ELEMENT);
			writer.writeAttribute(ODM130.MeasurementUnitRef.MEASUREMENT_UNIT_OID, measurementUnitRef.getMeasurementUnitOid().getValue());
			writer.writeEndElement();							
		}

		CodeListRef codeListRef = itemDef.getCodeList();
		if (codeListRef != null) {
			writer.writeStartElement(ODM130.NAMESPACE, ODM130.CodeListRef.ELEMENT);
			writer.writeAttribute(ODM130.CodeListRef.CODE_LIST_OID, codeListRef.getCodeListOid().getValue());
			writer.writeEndElement();							
		}
		
		writeTranslatedText(itemDef.getDescriptions());
		
		writer.writeEndElement();				
	}

	private void writeCodeList(CodeList codeList) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.CodeList.ELEMENT);

		OID oid = codeList.getOid();
		if (oid != null) {
			writer.writeAttribute(ODM130.CodeList.OID, oid.getValue());
		}
		
		DataType dataType = codeList.getDataType();
		if (dataType != null) {
			writer.writeAttribute(ODM130.CodeList.DATA_TYPE, dataType.getCode());
		}
		
		for (CodeListItem item : codeList.getItems()) {
			writeCodeListItem(item);
		}
		
		writer.writeEndElement();				
	}

	private void writeCodeListItem(CodeListItem item) throws XMLStreamException {
		writer.writeStartElement(ODM130.NAMESPACE, ODM130.CodeListItem.ELEMENT);

		String codedValue = item.getCodedValue();
		if (codedValue != null) {
			writer.writeAttribute(ODM130.CodeListItem.CODEDVALUE, codedValue);
		}
		
		int rank = item.getRank();
		if (rank != 0) {
			writer.writeAttribute(ODM130.CodeListItem.RANK, String.valueOf(rank));
		}

		writer.writeStartElement(ODM130.NAMESPACE, ODM130.CodeListItem.DECODE);		
		writeTranslatedText(item.getDecode());
		writer.writeEndElement();				
		
		writer.writeEndElement();				
	}
	
	private void writeTranslatedText(TranslatedTextCollection texts) throws XMLStreamException {
		for (TranslatedText text : texts) {
			writer.writeStartElement(ODM130.NAMESPACE, ODM130.TranslatedText.ELEMENT);
			writer.writeAttribute("xml", XMLNS, "lang", text.getLanguageTag());
			writer.writeCharacters(text.getText());
			writer.writeEndElement();
		}		
	}
}
