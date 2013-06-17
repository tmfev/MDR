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
 * Represents an Definition (7.3.2.4).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="DEFINITION")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@EntityListeners(IndexListener.class)
public class Definition implements HasKey, Indexable, Visitable {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length=4000)
	private String text;
	
	@ManyToOne(optional = true)
	private LanguageIdentification language;
	
	@OneToMany(mappedBy="definition", cascade=CascadeType.PERSIST)
	private Collection<DefinitionContext> scopes = new ArrayList<DefinitionContext>();
	
	@ManyToOne(optional=true)
	private Item item;

	@ManyToOne
	private ReferenceDocument sourceReference;
	
	public Definition() {
	}

	public Definition(String text) {
		if (text == null) throw new NullPointerException();		
		this.text = text;
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.Definition.URN, id);
	}
	
	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(text, this, item, EnumSet.of(IndexTokenReceiver.Options.NEVER_SPLIT));
	}	
	
	public void setItem(Item item) {
		this.item = item;
	}

	public LanguageIdentification getLanguage() {
		return language;
	}

	public void setLanguage(LanguageIdentification language) {
		this.language = language;
	}
	
	public Item getItem() {
		return item;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public ReferenceDocument getSourceReference() {
		return sourceReference;
	}
	
	public void setSourceReference(ReferenceDocument sourceReference) {
		this.sourceReference = sourceReference;
	}
	
	public void addScope(DefinitionContext scope) {
		scopes.add(scope);		
	}
	
	public Collection<DefinitionContext> getScopes() {
		return scopes;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
