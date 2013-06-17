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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents the relation between an {@link Assertion} and its associated {@link Concept}.
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "ASSERTION_END")
public class AssertionEnd implements HasKey, Visitable {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Concept end;

	@ManyToOne
	private Assertion assertion;

	public AssertionEnd() {
	}

	protected AssertionEnd(Concept end, Assertion assertion) {
		this.end = end;
		this.assertion = assertion;
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.AssertionEnd.URN, id);
	}
	
	public static AssertionEnd create(Concept concept, Assertion assertion) {
		AssertionEnd end = new AssertionEnd(concept, assertion);
		PMF.get().persist(end);
		assertion.addTerm(end);		
		return end;
	}	
	
	public Concept getEnd() {
		return end;
	}
	
	public void setEnd(Concept end) {
		this.end = end;
	}

	public Assertion getAssertion() {
		return assertion;
	}
	
	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
