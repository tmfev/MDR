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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.impexp.Imports;

/**
 * Represents a Reference_Document (6.3.6).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "REFERENCE_DOCUMENT")
// @EntityListeners(IndexListener.class)
public class ReferenceDocument extends Item implements Indexable, Visitable {

	private String identifier;

	private String typeDescription;

	@ManyToOne
	private LanguageIdentification language;

	private String title;

	private String uri;

	public ReferenceDocument() {
	}

	public static ReferenceDocument create(String title, String uri) {
		ReferenceDocument document = new ReferenceDocument();
		document.title = title;
		document.uri = uri;

		PMF.get().persist(document);
		return document;
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ReferenceDocument.URN);
	}

	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(identifier, this, this);
		receiver.tokenize(typeDescription, this, this);
		receiver.tokenize(title, this, this);
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setLanguage(LanguageIdentification language) {
		this.language = language;
	}

	public LanguageIdentification getLanguage() {
		return language;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public ReferenceDocument importWith(ReferenceDocument format) {
		return Imports.doImports(this, format);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
