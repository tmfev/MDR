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

public interface Visitor {

	void visit(Assertion assertion);

	void visit(AssertionEnd assertionEnd);

	void visit(RelationRole relationRole);

	void visit(Classification classification);	
	
	void visit(Concept concept);

	void visit(ConceptSystem conceptSystem);
	
	void visit(ConceptualDomain conceptualDomain);
	
	void visit(Context context);

	void visit(DataElement dataElement);
	
	void visit(DataElementConcept dataElementConcept);

	void visit(DataType dataType);
	
	void visit(Definition definition);
	
	void visit(DefinitionContext definitionContext);	
	
	void visit(Designation designation);

	void visit(DesignationContext designationContext);

	void visit(Dimensionality dimensionality);
	
	void visit(LanguageIdentification languageIdentification);
	
	void visit(Link link);
	
	void visit(LinkEnd linkEnd);
	
	void visit(Namespace namespace);
	
	void visit(ObjectClass objectClass);
	
	void visit(PermissibleValue permissibleValue);
	
	void visit(ReferenceDocument referenceDocument);
	
	void visit(Relation relation);
	
	void visit(ScopedIdentifier scopedIdentifier);
	
	void visit(Slot slot);
	
	void visit(UnitOfMeasure unitOfMeasure);
	
	void visit(ValueDomain valueDomain);
	
	void visit(ValueMeaning valueMeaning);	

	void visit(User subject);

	void visit(UserGroup subjectGroup);
	
	void visit(Rating rating);

	void visit(LoginInfo loginInfo);

	void visit(ResultList resultList);	
}
