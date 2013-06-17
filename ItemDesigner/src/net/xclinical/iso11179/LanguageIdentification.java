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

import java.util.EnumSet;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.IndexListener;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.IndexTokenReceiver.Options;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Language_Identification (6.3.4).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "LANGUAGE_IDENTIFICATION")
@EntityListeners(IndexListener.class)
public class LanguageIdentification extends Item implements Indexable {

	public static final String EN_US = "en-US";

	private String languageIdentifier;

	public LanguageIdentification() {
	}

	private LanguageIdentification(String languageIdentifier) {
		this();
		this.languageIdentifier = languageIdentifier;
	}

	public static LanguageIdentification create(String identifier) {
		LanguageIdentification lang = new LanguageIdentification(identifier);
		PMF.get().persist(lang);
		return lang;
	}

	public static LanguageIdentification findOrCreate(String identifier) {
		Query query = PMF.get().createQuery("select l from LanguageIdentification l where l.languageIdentifier = :id")
				.setParameter("id", identifier);

		try {
			return (LanguageIdentification) query.getSingleResult();
		} catch (NoResultException e) {
			return create(identifier);
		}
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.LanguageIdentification.URN);
	}

	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(languageIdentifier, this, this, EnumSet.of(Options.NEVER_SPLIT));
	}

	public void setLanguageIdentifier(String languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	public String getLanguageIdentifier() {
		return languageIdentifier;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
