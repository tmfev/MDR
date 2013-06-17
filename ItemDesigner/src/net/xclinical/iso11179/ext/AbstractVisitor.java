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
package net.xclinical.iso11179.ext;

import net.xclinical.iso11179.Assertion;
import net.xclinical.iso11179.AssertionEnd;
import net.xclinical.iso11179.Classification;
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

public class AbstractVisitor implements Visitor {

	@Override
	public void visit(Assertion assertion) {
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
	}

	@Override
	public void visit(RelationRole relationRole) {
	}

	@Override
	public void visit(Classification classification) {
	}
	
	@Override
	public void visit(Concept concept) {
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
	}

	@Override
	public void visit(Context context) {
	}

	@Override
	public void visit(DataElement dataElement) {
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
	}

	@Override
	public void visit(DataType dataType) {
	}

	@Override
	public void visit(Definition definition) {
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
	}

	@Override
	public void visit(Designation designation) {
	}

	@Override
	public void visit(DesignationContext designationContext) {
	}

	@Override
	public void visit(Dimensionality dimensionality) {
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
	}

	@Override
	public void visit(Link link) {
	}

	@Override
	public void visit(LinkEnd linkEnd) {
	}

	@Override
	public void visit(Namespace namespace) {
	}

	@Override
	public void visit(ObjectClass objectClass) {
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
	}

	@Override
	public void visit(Relation relation) {
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
	}

	@Override
	public void visit(Slot slot) {
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
	}

	@Override
	public void visit(ValueDomain valueDomain) {
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
	}

	@Override
	public void visit(User subject) {
	}

	@Override
	public void visit(UserGroup subjectGroup) {
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

}
