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
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Conceptual_Domain (11.1.2.2)
 * 
 * <p>This class represents the Conceptual_Domain class of ISO11179, as specified in chapter 11.3.2.1
 * <p>Since Java does not support overlapping types and the ISO11179 classes Enumerated_Conceptual_Domain
 * and Described_Conceptual_Domain are overlapping, we decided to merge the two classes into this class.
 * <p>For the time being, we define a Conceptual_Domain as being an Enumerated_Conceptual_Domain if, and
 * only if it has members (e.g. {@link #getMembers()} returns a collection that is not {@link Collection#isEmpty()}.
 * If an explicit distinction is desired in future versions, an additional field could be used.
 * 
 * @author ms@xclinical.com
 *
 */
@Entity
@Table(name="CONCEPTUAL_DOMAIN")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ConceptualDomain.USAGES, query = "select d from DataElementConcept d where ?1 member of d.domains"), 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ConceptualDomain.REPRESENTATIONS, query = "select d from ValueDomain d where d.meaning = ?1"), 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ConceptualDomain.MEMBERS, query = "select m from ValueMeaning m where ?1 member of m.containingDomain") 
})
public class ConceptualDomain extends Concept {

	private String description;
	
	@OneToMany(mappedBy="meaning")
	private Collection<ValueDomain> representations = new HashSet<ValueDomain>();
	
	@ManyToMany // mappedBy="domains"
	private Collection<DataElementConcept> usages = new ArrayList<DataElementConcept>();
	
	@ManyToMany // mappedBy="containingDomain"
	private Collection<ValueMeaning> members = new HashSet<ValueMeaning>();
	
	public ConceptualDomain() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ConceptualDomain.URN);
	}
	
	public static ConceptualDomain create(Context ctx, String sign, LanguageIdentification language) {
		ConceptualDomain element = new ConceptualDomain();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}	

	/**
	 * Retrieves the description
	 * 
	 * This method retrieves the description as defined in ISO11179-V3, chapter 11.3.2.4.3.1
	 *  
	 * @return the description of this Described_Conceptual_Domain
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Changes the description
	 * 
	 * @see #getDescription()
	 * @param description is the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	void addUsage(DataElementConcept concept) {
		usages.add(concept);
	}
	
	void addRepresentation(ValueDomain representation) {
		representations.add(representation);
	}
		
	public Collection<ValueDomain> getRepresentations() {
		return representations;
	}
	
	public Collection<DataElementConcept> getUsages() {
		return usages;
	}
	
	public void addMember(ValueMeaning meaning) {
		members.add(meaning);
	}
	
	public Collection<ValueMeaning> getMembers() {
		return members;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
