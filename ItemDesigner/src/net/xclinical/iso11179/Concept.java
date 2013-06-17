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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;

/**
 * Represents an Concept (9.1.2.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="CONCEPT")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Concept.ASSERTIONS, query = "select e.assertion from AssertionEnd e where e.end = ?1"),
	
	// All relation roles existing on this concept (concept)
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Concept.RELATION_ROLES, query = 
			"select distinct e.endRoles from LinkEnd e where e.assertion in (select e.assertion from LinkEnd e where e.end = ?1)"),
	
	// All related concepts (concept, relation role)
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Concept.RELATED, query = 
			"select distinct e.end from LinkEnd e where e.end != ?1 and e.assertion in (select e.assertion from LinkEnd e where e.end = ?1) and ?2 member of e.endRoles") 
})
public class Concept extends Item {

	@ManyToOne
	private ConceptSystem including;
	
	@OneToMany(targetEntity=AssertionEnd.class, mappedBy="end")
	private Collection<AssertionEnd> assertions = new ArrayList<AssertionEnd>();	
	
	public Concept() {
	}	
	
	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Concept.URN);
	}
	
	public void setIncluding(ConceptSystem including) {
		this.including = including;
	}
	
	public ConceptSystem getIncluding() {
		return including;
	}

	public Collection<AssertionEnd> getAssertions() {
		return assertions;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
