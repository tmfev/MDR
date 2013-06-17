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
package com.xclinical.mdr.server.impexp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import net.xclinical.iso11179.Characteristic;
import net.xclinical.iso11179.ConceptualDomain;
import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.DataElementConcept;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.ObjectClass;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;

import com.xclinical.mdr.repository.ItemRepository;
import com.xclinical.mdr.repository.RepositoryStoreException;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.server.xml.XmlRow;
import com.xclinical.mdr.server.xml.XmlTable;
import com.xclinical.mdr.util.HashTree;

/**
 * DataElementConcept
 * <Dokumentationseinheit>Geburt</Dokumentationseinheit>->ObjectClass 
 * <Bezeichnung>Geburtsgewicht</Bezeichnung>->Characteristic 
 * <Beschreibung>Gramm</Beschreibung>
 * <ClinSurv>0</ClinSurv> <Serokonverter>0</Serokonverter>
 * <Modul_x0020_Basis>0</Modul_x0020_Basis>
 * <Modul_x0020_Basis_x0020_Kinder
 * >1</Modul_x0020_Basis_x0020_Kinder>
 * <Modul_x0020_Frauen>0</Modul_x0020_Frauen>
 * <Modul_x0020_Gastroenterologie
 * >0</Modul_x0020_Gastroenterologie>
 * <Modul_x0020_Hepatitis>0</Modul_x0020_Hepatitis>
 * <Modul_x0020_Metabolische_x0020_Störungen
 * >0</Modul_x0020_Metabolische_x0020_Störungen>
 * <Modul_x0020_Neurologie>0</Modul_x0020_Neurologie>
 * 
 * @author ms
 *
 */
public class KnHivAidsImporter implements Importer {

	private static final Logger LOG = Logger.get(KnHivAidsImporter.class);

	private ResourceResolver resources;
	
	private ReferenceDocument referenceDocument;

	private LanguageIdentification de;
	
	private Context createModule(Context catalog, String name) throws RepositoryStoreException {
		Context module = Context.create(catalog, name, de);				
		return module;
	}

	/**
	 * Recursively creates domains from a tree of nodes.
	 */
	private ValueDomain deflateEnumeration(Context context, String name, HashTree<String> tree) {
		Set<String> keySet = tree.get();
		
		if (keySet.size() > 0) {
			// Either a superdomain or a value domain. 
			ConceptualDomain conceptualDomain = ConceptualDomain.create(context, name, de);			
			ValueDomain valueDomain = ValueDomain.create(context, name, de);
			valueDomain.setMeaning(conceptualDomain);

			for (String designation : keySet) {
				if (designation == null) {
					LOG.info("Ignoring null designation in " + name);
					continue;
				}

				ValueDomain subDomain = deflateEnumeration(context, designation, tree.subTree(designation));
				if (subDomain != null) {
					valueDomain.addSubDomain(subDomain);
				}
				else {
					ValueMeaning meaning = ValueMeaning.create(context, designation, de);
					conceptualDomain.addMember(meaning);
					PermissibleValue value = PermissibleValue.create(meaning, designation);
					valueDomain.addMember(value);					
				}				
			}			
		
			return valueDomain;
		}
		else {
			return null;
		}
	}
	
	private void loadEnumeratedDomain(Map<String, ConceptualDomain> map, Context context, String conceptName) throws Exception {
		ConceptualDomain enumDomain = ConceptualDomain.create(context, conceptName, de);

		ValueDomain valueDomain = ValueDomain.create(context, "Elements of " + conceptName, de);
		valueDomain.setMeaning(enumDomain);
		
		String fullConceptName = "Stammdaten " + conceptName;
		XmlTable xml = new XmlTable(resources.open(fullConceptName + ".xml"), "dataroot");
		
		HashTree<String> tree = new HashTree<String>();
		
		XmlRow row;
		while ((row = xml.nextRow()) != null) {
			String group = row.get("Gruppe");
			String designation = row.get("Bezeichnung");
			String name = row.get("Freiname");
			String tradeName = row.get("Handelsname");

			if (name != null) {
				if (tradeName != null) {
					tree.put(group, name, tradeName);
				}
				else {
					tree.put(group, name);					
				}
			}
			else if (group != null) {
				tree.put(group, designation);
			}
			else {
				tree.put(designation);				
			}
		}

		map.put(fullConceptName, deflateEnumeration(context, fullConceptName, tree).getMeaning());
	}
	
	@Override
	public void read(final ResourceResolver resources, final String uri) throws IOException {
		this.resources = resources;
		
		try {
			PMF.run(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					ItemRepository repository = null; //PMF.get();

					referenceDocument = ReferenceDocument.create("Merkmalskatalog HIVNET", uri);
					
					/*
						<Stammdaten_x0020_Anamnese>
						<Gruppe>Eigenanamnese</Gruppe>
						<Bezeichnung>Kinderkrankheiten</Bezeichnung>
						<Modul_x0020_Basis>0</Modul_x0020_Basis>
						<Modul_x0020_Basis_x0020_Kinder>0</Modul_x0020_Basis_x0020_Kinder>
						<Modul_x0020_Frauen>1</Modul_x0020_Frauen>
						<Modul_x0020_Gastroenterologie>0</Modul_x0020_Gastroenterologie>
						<Modul_x0020_Hepatitis>0</Modul_x0020_Hepatitis>
						<Modul_x0020_Metabolische_x0020_Störungen>0</Modul_x0020_Metabolische_x0020_Störungen>
						<Modul_x0020_Neurologie>0</Modul_x0020_Neurologie>
						</Stammdaten_x0020_Anamnese>				 
					 */
					
					XmlTable assertions = new XmlTable(KnHivAidsImporter.this.resources.open("Merkmal.xml"), "dataroot");

					de = LanguageIdentification.create("de");
					
					Context mdr = Context.create("MDR", de);
					
					Context catalog = Context.create(mdr, "Merkmalskatalog HIVNET einfach", de);
					Definition def = mdr.define(catalog, "XML Import");
					def.setSourceReference(referenceDocument);
					
					Map<String, ObjectClass> documentationUnits = new HashMap<String, ObjectClass>();
					Map<String, Context> modules = new HashMap<String, Context>();
					Map<String, ConceptualDomain> conceptualDomains = new HashMap<String, ConceptualDomain>();

					modules.put("Modul_x0020_Basis", createModule(catalog, "Modul Basis"));
					modules.put("Modul_x0020_Basis_x0020_Kinder", createModule(catalog, "Modul Basis Kinder"));
					modules.put("Modul_x0020_Frauen", createModule(catalog, "Modul Frauen"));
					modules.put("Modul_x0020_Gastroenterologie", createModule(catalog, "Modul Gastroenterologie"));
					modules.put("Modul_x0020_Hepatitis", createModule(catalog, "Modul Hepatitis"));
					modules.put("Modul_x0020_Metabolische_x0020_Störungen", createModule(catalog, "Modul Metabolische Störungen"));
					modules.put("Modul_x0020_Neurologie", createModule(catalog, "Modul Neurologie"));

					loadEnumeratedDomain(conceptualDomains, catalog, "Anamnese");
					loadEnumeratedDomain(conceptualDomains, catalog, "Applikationsform");
					loadEnumeratedDomain(conceptualDomains, catalog, "Arzneimittel");
					loadEnumeratedDomain(conceptualDomains, catalog, "Berufsausbildung");
					loadEnumeratedDomain(conceptualDomains, catalog, "Berufstätigkeit");
					loadEnumeratedDomain(conceptualDomains, catalog, "Diagnosen");
					loadEnumeratedDomain(conceptualDomains, catalog, "Entbindungsmodus");
					loadEnumeratedDomain(conceptualDomains, catalog, "Erreger");
					loadEnumeratedDomain(conceptualDomains, catalog, "Familienstand");
					loadEnumeratedDomain(conceptualDomains, catalog, "Geschlecht");
					loadEnumeratedDomain(conceptualDomains, catalog, "HIV-Typ");
					loadEnumeratedDomain(conceptualDomains, catalog, "Infektionsrisiko");
					loadEnumeratedDomain(conceptualDomains, catalog, "Materialbanken");
					loadEnumeratedDomain(conceptualDomains, catalog, "Nation");
					loadEnumeratedDomain(conceptualDomains, catalog, "Präparationsmethoden");
					loadEnumeratedDomain(conceptualDomains, catalog, "Prozedur");
					loadEnumeratedDomain(conceptualDomains, catalog, "Schulabschluss");
					loadEnumeratedDomain(conceptualDomains, catalog, "Situation");
					loadEnumeratedDomain(conceptualDomains, catalog, "Symptom");
					loadEnumeratedDomain(conceptualDomains, catalog, "Therapieabbruch");
					loadEnumeratedDomain(conceptualDomains, catalog, "Vitalstatus");
					loadEnumeratedDomain(conceptualDomains, catalog, "Zusammenhang");
					loadEnumeratedDomain(conceptualDomains, catalog, "diagnostische Verfahren");
					loadEnumeratedDomain(conceptualDomains, catalog, "meldende Einrichtungen");
					
					XmlRow row;
					while ((row = assertions.nextRow("Merkmal")) != null) {

						//
						String documentationUnitName = row.get("Dokumentationseinheit");
						ObjectClass documentationUnit = documentationUnits.get(documentationUnitName);
						if (documentationUnit == null) {
							documentationUnit = ObjectClass.create(catalog, documentationUnitName, de);
							documentationUnits.put(documentationUnitName, documentationUnit);
						}

						Characteristic characteristic = Characteristic.create(catalog, row.get("Bezeichnung"), de);
						
						DataElementConcept concept = DataElementConcept.create(documentationUnit, characteristic);					
						catalog.designate(concept, row.get("Bezeichnung"), de);

						// <Beschreibung>{Unit or Description}
						// <Werteliste>1|2|3
						// <Werteliste>Stammdaten XXX
						String description = row.get("Beschreibung");
						if (description != null) {
							ConceptualDomain describedDomain = ConceptualDomain.create(catalog, description, de);
							ValueDomain valueDomain = ValueDomain.create(catalog, description, de);
							valueDomain.setMeaning(describedDomain);

							concept.addConceptualDomain(describedDomain);							
						}
							
						String values = row.get("Werteliste");
						if (values != null) {
							ConceptualDomain conceptualDomain = conceptualDomains.get(values);
							if (conceptualDomain == null) {
								ConceptualDomain enumDomain = ConceptualDomain.create(catalog, "Werte", de);
								ValueDomain valueDomain = ValueDomain.create(catalog, "values", de);
								valueDomain.setMeaning(enumDomain);

								for (String value : values.split("\\|")) {
									ValueMeaning meaning = ValueMeaning.create(catalog, value, de);
									enumDomain.addMember(meaning);
									valueDomain.addMember(PermissibleValue.create(meaning, value));
								}
								
								conceptualDomain = enumDomain;
								conceptualDomains.put(values, conceptualDomain);
							}
							concept.addConceptualDomain(conceptualDomain);
						}
						
						Designation documentationUnitDesignation = documentationUnit.getDesignations().iterator().next();
						
						for (Map.Entry<String, Context> e : modules.entrySet()) {
							Context module = e.getValue();

							if ("1".equals(row.get(e.getKey()))) {
								module.addRelevantDesignation(documentationUnitDesignation);
							}
						}					
					}
					
					repository.persist(mdr);
					return null;
				}
			});
		} catch (InvocationTargetException e) {
			throw new IOException(e);
		}
	}
}
