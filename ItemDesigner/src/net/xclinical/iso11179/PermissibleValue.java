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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Permissible_Value (11.3.2.7).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="PERM_VALUE")
public class PermissibleValue extends Item {

	@ManyToOne
	private ValueMeaning meaning;
	
	@ManyToMany
	private Set<ValueDomain> containingDomains = new HashSet<ValueDomain>();
	
	private String permittedValue;
	
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name="BEGIN_DATE")
	private Date begin;
	
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name="END_DATE")
	private Date end;
	
	public PermissibleValue() {
	}

	public static PermissibleValue create(ValueMeaning meaning, String permittedValue) {
		PermissibleValue value = new PermissibleValue();
		value.setPermittedValue(permittedValue);
		meaning.addRepresentation(value);
		PMF.get().persist(value);
		return value;
	}

	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.PermissibleValue.URN, getId());
	}
	
	public void setMeaning(ValueMeaning meaning) {
		this.meaning = meaning;
	}
	
	public ValueMeaning getMeaning() {
		return meaning;
	}
	
	public void addContainingDomain(ValueDomain containingDomain) {
		this.containingDomains.add(containingDomain);
	}
	
	public Set<ValueDomain> getContainingDomains() {
		return containingDomains;
	}
	
	public void setPermittedValue(String permittedValue) {
		this.permittedValue = permittedValue;
	}

	public String getPermittedValue() {
		return permittedValue;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getBegin() {
		return begin;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
