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

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Data_Element_Concept (11.1.2.5).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "DATA_ELEMENT_CONCEPT")
@NamedQueries({
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.DataElementConcept.CONCEPTUAL_DOMAINS, query = "select d from ConceptualDomain d where ?1 in d.usages"),
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.DataElementConcept.REPRESENTATIONS, query = "select d from DataElement d where d.meaning = ?1")
})
public class DataElementConcept extends Concept {

	@ManyToOne(optional = true)
	private ObjectClass objectClass;

	@ManyToOne(optional = true)
	private Characteristic characteristic;

	@ManyToMany(mappedBy = "usages")
	private Collection<ConceptualDomain> domains = new ArrayList<ConceptualDomain>();

	@OneToMany(mappedBy = "meaning")
	private Collection<DataElement> representations = new ArrayList<DataElement>();

	public DataElementConcept() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.DataElementConcept.URN);
	}

	public ObjectClass getObjectClass() {
		return objectClass;
	}

	public Characteristic getCharacteristic() {
		return characteristic;
	}

	public void addConceptualDomain(ConceptualDomain domain) {
		if (domain == null) throw new NullPointerException();
		
		domains.add(domain);
		domain.addUsage(this);
	}

	public Collection<ConceptualDomain> getConceptualDomains() {
		return domains;
	}

	public void addRepresentation(DataElement dataElement) {
		dataElement.setMeaning(this);
		representations.add(dataElement);
	}

	public Collection<DataElement> getRepresentations() {
		return representations;
	}

	public static DataElementConcept create(ObjectClass obj, Characteristic characteristic) {
		if (obj == null)
			throw new NullPointerException();
		if (characteristic == null)
			throw new NullPointerException();

		DataElementConcept concept = new DataElementConcept();
		PMF.get().persist(concept);

		concept.objectClass = obj;
		concept.characteristic = characteristic;

		obj.addConcept(concept);

		return concept;
	}
}
