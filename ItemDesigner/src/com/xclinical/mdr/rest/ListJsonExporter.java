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

import java.util.Collection;

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

public class ListJsonExporter extends AbstractVisitor {

	private final String name;
	
	private final JsonArray list;

	private ListJsonExporter(String name) {
		this(new JsonArray(), name);
	}

	private ListJsonExporter(JsonArray list, String name) {
		this.list = list;
		this.name = name;
	}
	
	public static JsonArray of(Visitable element, String name) {
		ListJsonExporter ex = new ListJsonExporter(name);
		element.accept(ex);
		return ex.list;		
	}

	private boolean isName(String name) {
		return this.name.equals(name);		
	}
	
	private void add(Visitable v) {
		list.add(FlatJsonExporter.of(v));		
	}
	
	private void addAll(String name, Collection<? extends Visitable> c) {
		if (isName(name)) {
			for (Visitable v : c) {
				add(v);
			}
		}		
	}
	
	private void visitItem(Item item) {
		addAll("designations", item.getDesignations());
		addAll("definitions", item.getDefinitions());
		addAll("scopedIdentifiers", item.getScopedIdentifiers());
	}

	@Override
	public void visit(Assertion assertion) {
		visitItem(assertion);

		addAll("terms", assertion.getTerms());
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
	}

	@Override
	public void visit(RelationRole relationRole) {
		visit((Concept) relationRole);
		addAll("linkEnds", relationRole.getLinkEnds());
	}

	@Override
	public void visit(Concept concept) {
		visitItem(concept);
		addAll("assertions", concept.getAssertions());
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		visitItem(conceptSystem);
		addAll("includedAssertions", conceptSystem.getIncludedAssertions());
		addAll("members", conceptSystem.getMembers());
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		visit((Concept) conceptualDomain);
		addAll("representations", conceptualDomain.getRepresentations());
		addAll("usages", conceptualDomain.getUsages());
		addAll("members", conceptualDomain.getMembers());
	}

	@Override
	public void visit(Context context) {
		visitItem(context);
		
		if (isName("relevantDesignations")) {
			for (DesignationContext c : context.getRelevantDesignations()) {
				add(c.getDesignation());
			}
		}

		if (isName("relevantDefinition")) {
			for (DefinitionContext c : context.getRelevantDefinitions()) {
				add(c.getDefinition());
			}
		}		
	}

	@Override
	public void visit(DataElement dataElement) {
		visitItem(dataElement);
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		visit((Concept) dataElementConcept);
		addAll("domains", dataElementConcept.getConceptualDomains());
		addAll("representations", dataElementConcept.getRepresentations());
	}

	@Override
	public void visit(DataType dataType) {
		visitItem(dataType);
	}

	@Override
	public void visit(Definition definition) {
		addAll("scopes", definition.getScopes());
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
	}

	@Override
	public void visit(Designation designation) {
		addAll("scopes", designation.getScopes());
	}

	@Override
	public void visit(DesignationContext designationContext) {
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		visit((Concept) dimensionality);
		addAll("applicableUnits", dimensionality.getApplicableUnits());
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		visitItem(languageIdentification);
	}

	@Override
	public void visit(Link link) {
		visit((Assertion) link);
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		visit((AssertionEnd) linkEnd);
		addAll("endRoles", linkEnd.getEndRoles());
	}

	@Override
	public void visit(Namespace namespace) {
		visitItem(namespace);
	}

	@Override
	public void visit(ObjectClass objectClass) {
		visit((Concept) objectClass);
		addAll("concepts", objectClass.getConcepts());
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		addAll("containingDomains", permissibleValue.getContainingDomains());
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		visitItem(referenceDocument);
	}

	@Override
	public void visit(Relation relation) {
		visit((Concept)relation);
		addAll("roles", relation.getRoles());
		addAll("links", relation.getLinks());
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
	}

	@Override
	public void visit(Slot slot) {
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		visit((Concept)unitOfMeasure);
	}

	@Override
	public void visit(ValueDomain valueDomain) {
		visitItem(valueDomain);
		addAll("usages", valueDomain.getUsages());
		addAll("subDomains", valueDomain.getSubDomains());
		addAll("members", valueDomain.getMembers());
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		visit((Concept)valueMeaning);
		addAll("representations", valueMeaning.getRepresentations());
		addAll("containingDomains", valueMeaning.getContainingDomain());
	}

	@Override
	public void visit(Rating rating) {
	}
	
	@Override
	public void visit(LoginInfo loginInfo) {
	}
	
	@Override
	public void visit(ResultList resultList) {
	}
	
	@Override
	public void visit(User subject) {
		visitItem(subject);
	}
	
	@Override
	public void visit(UserGroup subjectGroup) {
		visitItem(subjectGroup);
	}
}
