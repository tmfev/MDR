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
 * Represents an Assertion (9.1.2.3).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "ASSERTI0N")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Assertion.TERMS, query = "select e.end from AssertionEnd e where e.assertion = ?1") 
})
public class Assertion extends Item {

	@ManyToOne
	private ConceptSystem assertor;

	@OneToMany(targetEntity=AssertionEnd.class, mappedBy="assertion")
	private Collection<AssertionEnd> terms = new ArrayList<AssertionEnd>();
	
	private String formula;

	public Assertion() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Assertion.URN);
	}

	public Collection<AssertionEnd> getTerms() {
		return terms;
	}
	
	public void addTerm(AssertionEnd end) {
		end.setAssertion(this);
		terms.add(end);
	}
	
	public void setAssertor(ConceptSystem assertor) {
		this.assertor = assertor;
	}

	public ConceptSystem getAssertor() {
		return assertor;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFormula() {
		return formula;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
