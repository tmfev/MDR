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
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Value_Domain (11.1.2.3).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "VALUE_DOMAIN")
@NamedQueries({ @NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ValueDomain.MEMBERS, query = "select v from PermissibleValue v where ?1 member of v.containingDomains") })
public class ValueDomain extends Item {

	private Integer maximumCharacterQuantity;

	@ManyToOne(optional = true)
	private DataType dataType;

	private String format;
	
	@ManyToOne
	private ConceptualDomain meaning;

	@OneToMany(mappedBy = "domain")
	private Collection<DataElement> usages = new ArrayList<DataElement>();

	@OneToOne(optional = true)
	private UnitOfMeasure unitOfMeasure;

	@OneToMany
	private Collection<ValueDomain> subDomains = new HashSet<ValueDomain>();

	@ManyToMany(mappedBy = "containingDomains")
	private Set<PermissibleValue> members = new HashSet<PermissibleValue>();

	public ValueDomain() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ValueDomain.URN);
	}

	public Integer getMaximumCharacterQuantity() {
		return maximumCharacterQuantity;
	}

	public void setMaximumCharacterQuantity(Integer maximumCharacterQuantity) {
		this.maximumCharacterQuantity = maximumCharacterQuantity;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void addUsage(DataElement dataElement) {
		dataElement.setDomain(this);
		usages.add(dataElement);
	}

	public Collection<DataElement> getUsages() {
		return usages;
	}

	public void addSubDomain(ValueDomain subDomain) {
		subDomains.add(subDomain);
	}

	public Collection<ValueDomain> getSubDomains() {
		return subDomains;
	}

	public UnitOfMeasure getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public void setMeaning(ConceptualDomain meaning) {
		if (meaning != null) {
			meaning.addRepresentation(this);
			this.meaning = meaning;
		}
	}

	public ConceptualDomain getMeaning() {
		return meaning;
	}

	public static ValueDomain create(Context ctx, String sign, LanguageIdentification language) {
		ValueDomain element = new ValueDomain();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}

	public void addMember(PermissibleValue member) {
		member.addContainingDomain(this);
		members.add(member);
	}

	public Set<PermissibleValue> getMembers() {
		return members;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
