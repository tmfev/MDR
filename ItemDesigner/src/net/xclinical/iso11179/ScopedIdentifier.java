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

import javax.persistence.CascadeType;
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
 * Represents a Scoped_Identifier (7.2.2.2).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="SCOPED_IDENTIFIER")
public class ScopedIdentifier implements HasKey, Visitable {

	@Id
	@GeneratedValue
	private Long id;
	
	private String identifier;

	private String fullExpansion;
	
	private String shorthandExpansion;
	
	@ManyToOne(optional=true, cascade = CascadeType.PERSIST)
	private Namespace scope;
	
	@ManyToOne(optional=true)
	private Item item;
	
	public ScopedIdentifier() {		
	}

	public static ScopedIdentifier create(Namespace scope, String identifier) {
		ScopedIdentifier i = new ScopedIdentifier();
		i.setScope(scope);
		i.setIdentifier(identifier);
		PMF.get().persist(i);
		return i;
	}

	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.ScopedIdentifier.URN, id);
	}
	
	public Long getId() {
		return id;
	}

	void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setFullExpansion(String fullExpansion) {
		this.fullExpansion = fullExpansion;
	}

	public String getFullExpansion() {
		return fullExpansion;
	}

	public void setShorthandExpansion(String shorthandExpansion) {
		this.shorthandExpansion = shorthandExpansion;
	}

	public String getShorthandExpansion() {
		return shorthandExpansion;
	}

	public void setScope(Namespace scope) {
		this.scope = scope;
	}

	public Namespace getScope() {
		return scope;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
