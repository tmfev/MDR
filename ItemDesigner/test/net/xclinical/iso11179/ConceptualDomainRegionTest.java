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
package net.xclinical.iso11179;

import java.util.concurrent.Callable;

import com.xclinical.mdr.server.PMF;

import junit.framework.TestCase;

public class ConceptualDomainRegionTest extends TestCase {

	public void testSimple() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				LanguageIdentification de = LanguageIdentification.create("ww");
				
				Context catalog = Context.create("catalog", de);
				Characteristic characteristic = Characteristic.create(catalog, "Bezeichnung", de);
				
				ObjectClass documentationUnit = ObjectClass.create(catalog, "Patient", de);
				
				DataElementConcept concept = DataElementConcept.create(documentationUnit, characteristic);					
				catalog.designate(concept, "Blood Pressure", de);

				ConceptualDomain enumDomain = ConceptualDomain.create(catalog, "Werte", de);
				ValueMeaning valueMeaning = ValueMeaning.create(catalog, "Meaning", de);
				enumDomain.addMember(valueMeaning);
				
				/*
				concept.addConceptualDomain(enumDomain);
				*/
				
				return null;
			}
		});		
	}
}
