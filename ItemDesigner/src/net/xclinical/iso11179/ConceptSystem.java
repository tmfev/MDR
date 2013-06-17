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

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.repository.RepositoryStoreException;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Concept_System (9.1.2.2).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="CONCEPT_SYSTEM")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ConceptSystem.INCLUDED_ASSERTIONS, query = "select a from Assertion a where a.assertor = ?1"), 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ConceptSystem.MEMBERS, query = "select c from Concept c where c.including = ?1") 	
})
public class ConceptSystem extends Item {

	@OneToMany(mappedBy="assertor")
	private Collection<Assertion> includedAssertions = new HashSet<Assertion>();

	@OneToMany(mappedBy="including")
	private Collection<Concept> members = new HashSet<Concept>();
	
	public ConceptSystem() {
	}
	
	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ConceptSystem.URN);
	}
	
	public static ConceptSystem create(Context context, String sign, LanguageIdentification language) throws RepositoryStoreException {
		ConceptSystem conceptSystem = new ConceptSystem();
		PMF.get().persist(conceptSystem);

		context.designate(conceptSystem, sign, language);
		return conceptSystem;
	}	

	public Collection<Assertion> getIncludedAssertions() {
		return includedAssertions;
	}
	
	public void addIncludedAssertion(Assertion assertion) {
		assertion.setAssertor(this);
		includedAssertions.add(assertion);
	}
	
	public Collection<Concept> getMembers() {
		return members;
	}
	
	public void addMember(Concept concept) {
		concept.setIncluding(this);
		members.add(concept);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
