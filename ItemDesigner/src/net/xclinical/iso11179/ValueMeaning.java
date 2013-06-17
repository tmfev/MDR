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
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Value_Meaning (11.3.2.3).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="VALUE_MEANING")
@NamedQueries({
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ValueMeaning.REPRESENTATIONS, query = "select v from PermissibleValue v where v.meaning = ?1") })
public class ValueMeaning extends Concept {
	
	@OneToMany(mappedBy="meaning")
	private Collection<PermissibleValue> representations = new HashSet<PermissibleValue>();

	@ManyToMany(mappedBy="members")
	private Collection<ConceptualDomain> containingDomain = new ArrayList<ConceptualDomain>();
	
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name="BEGIN_DATE")
	private Date begin;
	
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name="END_DATE")
	private Date end;
	
	public ValueMeaning() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ValueMeaning.URN);
	}
	
	public static ValueMeaning create(Context ctx, String sign, LanguageIdentification language) {
		ValueMeaning element = new ValueMeaning();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}
	
	public void addRepresentation(PermissibleValue representation) {
		representation.setMeaning(this);
		representations.add(representation);
	}

	public void addContainingDomain(ConceptualDomain domain) {
		domain.addMember(this);
		containingDomain.add(domain);
	}
	
	public Collection<PermissibleValue> getRepresentations() {
		return representations;
	}

	public Collection<ConceptualDomain> getContainingDomain() {
		return containingDomain;
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
}
