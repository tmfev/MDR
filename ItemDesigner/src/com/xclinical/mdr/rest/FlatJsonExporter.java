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
package com.xclinical.mdr.rest;

import java.util.List;

import net.xclinical.iso11179.Assertion;
import net.xclinical.iso11179.AssertionEnd;
import net.xclinical.iso11179.Concept;
import net.xclinical.iso11179.ConceptSystem;
import net.xclinical.iso11179.ConceptualDomain;
import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.DataElement;
import net.xclinical.iso11179.DataElementConcept;
import net.xclinical.iso11179.DataType;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.DefinitionContext;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.DesignationContext;
import net.xclinical.iso11179.Dimensionality;
import net.xclinical.iso11179.Item;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Link;
import net.xclinical.iso11179.LinkEnd;
import net.xclinical.iso11179.Namespace;
import net.xclinical.iso11179.ObjectClass;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.Relation;
import net.xclinical.iso11179.RelationRole;
import net.xclinical.iso11179.ScopedIdentifier;
import net.xclinical.iso11179.Slot;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;
import net.xclinical.iso11179.ext.AbstractVisitor;
import net.xclinical.iso11179.ext.LoginInfo;
import net.xclinical.iso11179.ext.Rating;
import net.xclinical.iso11179.ext.ResultList;
import net.xclinical.iso11179.ext.User;
import net.xclinical.iso11179.ext.UserGroup;
import net.xclinical.iso11179.ext.Visitable;

import com.mictale.jsonite.JsonArray;
import com.mictale.jsonite.JsonObject;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

public class FlatJsonExporter extends AbstractVisitor {

	private JsonObject obj;

	private FlatJsonExporter() {
		this(new JsonObject());
	}

	private FlatJsonExporter(JsonObject obj) {
		this.obj = obj;
	}
	
	public static JsonObject of(Visitable element) {
		FlatJsonExporter ex = new FlatJsonExporter();
		element.accept(ex);
		return ex.obj;		
	}

	public static JsonArray of(List<Object> elements) {
		JsonArray result = new JsonArray();
				
		for (Object obj : elements) {
			FlatJsonExporter ex = new FlatJsonExporter();
			((Visitable)obj).accept(ex);
			result.add(ex.obj);
		}
		
		return result;		
	}
	
	
	private void put(String name, HasKey key) {
		if (key == null) {
			obj.put(name, null);
		}
		else {
			Key k = key.getKey();
			obj.put(name, k.toString());
		}
	}

	private void put(String name, String value) {
		obj.put(name, value);
	}

	private void put(String name, Integer value) {
		obj.put(name, value);
	}

	private void put(String name, Float value) {
		obj.put(name, value);
	}
	
	private void put(String name, Boolean value) {
		obj.put(name, value);
	}
	
	private void visitItem(Item item) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, item.getKey());
	}

	@Override
	public void visit(Assertion assertion) {
		visitItem(assertion);
		put("formula", assertion.getFormula());
		put("assertor", assertion.getAssertor());
		put("formula", assertion.getFormula());
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, assertionEnd.getKey());
		put("end", assertionEnd.getEnd());
		put("assertion", assertionEnd.getAssertion());
	}

	@Override
	public void visit(RelationRole relationRole) {
		visit((Concept) relationRole);
		put("source", relationRole.getSource());
	}

	@Override
	public void visit(Concept concept) {
		visitItem(concept);
		put("including", concept.getIncluding());
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		visitItem(conceptSystem);
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		visit((Concept) conceptualDomain);

		put("description", conceptualDomain.getDescription());
	}

	@Override
	public void visit(Context context) {
		visitItem(context);
	}

	@Override
	public void visit(DataElement dataElement) {
		visitItem(dataElement);
		put("precision", dataElement.getPrecision());
		put("domain", dataElement.getDomain());
		put("meaning", dataElement.getMeaning());
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		visit((Concept) dataElementConcept);

		put("objectClass", dataElementConcept.getObjectClass());
		put("characteristic", dataElementConcept.getCharacteristic());
	}

	@Override
	public void visit(DataType dataType) {
		visitItem(dataType);
		put("name", dataType.getName());
		put("description", dataType.getDescription());
		put("schemeReference", dataType.getSchemeReference());
	}

	@Override
	public void visit(Definition definition) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, definition.getKey());
		put("text", definition.getText());
		put("displayName", definition.getText());
		put("language", (HasKey)definition.getLanguage());
		put("item", definition.getItem());
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
		put("definition", definitionContext.getDefinition());
		put("context", definitionContext.getContext());
	}

	@Override
	public void visit(Designation designation) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, designation.getKey());
		put("displayName", designation.getSign());
		put("sign", designation.getSign());
		put("language", (HasKey)designation.getLanguage());
		put("item", designation.getItem());
	}

	@Override
	public void visit(DesignationContext designationContext) {
		put("designation", designationContext.getDesignation());
		put("context", designationContext.getContext());
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		visit((Concept) dimensionality);
		put("coordinateIndicator", dimensionality.isCoordinateIndicator());
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		visitItem(languageIdentification);
		put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME,
				languageIdentification.getLanguageIdentifier());
		put("languageIdentifier", languageIdentification.getLanguageIdentifier());
	}

	@Override
	public void visit(Link link) {
		visit((Assertion) link);
		put("relation", link.getRelation());
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		visit((AssertionEnd) linkEnd);
	}

	@Override
	public void visit(Namespace namespace) {
		visitItem(namespace);

		put("oneNamePerItem", namespace.getOneNamePerItem());
		put("oneItemPerName", namespace.getOneItemPerName());
		put("mandatoryNamingConvention", namespace.getMandatoryNamingConvention());
		put("shorthandPrefix", namespace.getShorthandPrefix());
		put("namespaceSchemeReference", namespace.getNamespaceSchemeReference());
	}

	@Override
	public void visit(ObjectClass objectClass) {
		visit((Concept) objectClass);
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, permissibleValue.getKey());
		put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, permissibleValue.getPermittedValue());
		put("permittedValue", permissibleValue.getPermittedValue());
		put("meaning", permissibleValue.getMeaning());
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		visitItem(referenceDocument);

		put("identifier", referenceDocument.getIdentifier());
		put("typeDescription", referenceDocument.getTypeDescription());
		put("language", (HasKey)referenceDocument.getLanguage());
		put("title", referenceDocument.getTitle());
		put("uri", referenceDocument.getUri());

	}

	@Override
	public void visit(Relation relation) {
		visit((Concept)relation);
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, scopedIdentifier.getKey());
		put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, scopedIdentifier.getIdentifier());
		put("identifier", scopedIdentifier.getIdentifier());
		put("fullExpansion", scopedIdentifier.getFullExpansion());
		put("shorthandExpansion", scopedIdentifier.getShorthandExpansion());
	}

	@Override
	public void visit(Slot slot) {
		put(com.xclinical.mdr.client.iso11179.model.Item.ID, slot.getKey());
		put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, slot.getKey());
		put("key", slot.getKey());
		put("name", slot.getName());
		put("type", slot.getType());
		put("value", slot.getValue());
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		visit((Concept)unitOfMeasure);
	}

	@Override
	public void visit(ValueDomain valueDomain) {
		visitItem(valueDomain);
		
		put("maximumCharacterQuantity", valueDomain.getMaximumCharacterQuantity());
		put("dataType", valueDomain.getDataType());
		put("format", valueDomain.getFormat());
		put("meaning", valueDomain.getMeaning());
		put("unitOfMeasure", valueDomain.getUnitOfMeasure());
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		visit((Concept)valueMeaning);
	}

	@Override
	public void visit(Rating rating) {
		put("id", com.xclinical.mdr.client.iso11179.model.Rating.URN);
		put("target", rating.getTarget());
		put("averageRating", rating.getAverageValue());
		put("votes", rating.getVotes());
		put("rater", rating.getRater());
		put("value", rating.getValue());
	}
	
	@Override
	public void visit(LoginInfo loginInfo) {
		put("user", loginInfo.getUser());
		put("session", loginInfo.getSession());
	}
	
	@Override
	public void visit(ResultList resultList) {
	}
	
	@Override
	public void visit(User subject) {
		visitItem(subject);
		put("email", subject.getEmail());
	}
	
	@Override
	public void visit(UserGroup subjectGroup) {
		visitItem(subjectGroup);
	}
}
