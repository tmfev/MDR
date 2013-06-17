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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Characteristic (11.2.2.2).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "CHARACTERISTIC")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Characteristic.CONCEPTS, query = "select d from DataElementConcept d where d.characteristic = ?1") 
})
public class Characteristic extends Concept {

	@OneToMany(mappedBy = "characteristic")
	private Set<DataElementConcept> concepts = new HashSet<DataElementConcept>();

	public Characteristic() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Characteristic.URN);
	}

	public static Characteristic create(Context ctx, String sign, LanguageIdentification language) {
		Characteristic element = new Characteristic();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}

	public void addConcept(DataElementConcept concept) {
		concepts.add(concept);
	}

	public Set<DataElementConcept> getConcepts() {
		return concepts;
	}
}
