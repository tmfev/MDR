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
import java.util.EnumSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.IndexListener;
import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;

/**
 * Represents an Designation (7.3.2.3).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "DESIGNATION")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(IndexListener.class)
public class Designation implements HasKey, Indexable, Visitable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "SIGN", length=4000)
	private String sign;

	@ManyToOne(optional = true)
	private LanguageIdentification language;

	@OneToMany(mappedBy = "designation", cascade = CascadeType.PERSIST)
	private Collection<DesignationContext> scopes = new ArrayList<DesignationContext>();

	@ManyToOne(optional = true, cascade = CascadeType.PERSIST)
	private Item item;

	public Designation() {
	}

	public Designation(String sign, LanguageIdentification language) {
		if (sign == null)
			throw new NullPointerException();
		if (language == null)
			throw new NullPointerException();

		this.sign = sign;
		this.language = language;
	}

	public Long getId() {
		return id;
	}

	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.Designation.URN, id);
	}

	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(sign, this, item);
		receiver.tokenize(sign, this, item, EnumSet.of(IndexTokenReceiver.Options.NEVER_SPLIT));
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public LanguageIdentification getLanguage() {
		return language;
	}

	public void setLanguage(LanguageIdentification language) {
		this.language = language;
	}

	public void addScope(DesignationContext scope) {
		scopes.add(scope);
	}

	public void removeScope(DesignationContext scope) {
		scopes.remove(scope);
	}

	public Collection<DesignationContext> getScopes() {
		return scopes;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
