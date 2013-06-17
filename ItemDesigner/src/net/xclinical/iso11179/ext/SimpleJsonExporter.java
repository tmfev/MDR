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

import java.util.Iterator;

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

import org.json.JSONObject;

import com.xclinical.mdr.server.tree.JsonTreeWriter;
import com.xclinical.mdr.server.tree.TreeWriter;

/**
 * Retrieves a simple JSON from a {@link Visitable}, e.g. only key and display name.
 * 
 * @author ms@xclinical.net
 */
public class SimpleJsonExporter extends AbstractVisitor {

	private JsonTreeWriter writer;

	private SimpleJsonExporter() {
		this(new JsonTreeWriter());
	}

	private SimpleJsonExporter(JsonTreeWriter writer) {
		this.writer = writer;
	}
	
	public static void copy(TreeWriter writer, Visitable element) {
		SimpleJsonExporter ex = new SimpleJsonExporter((JsonTreeWriter)writer);
		element.accept(ex);		
	}
	
	public static JSONObject of(Visitable element) {
		SimpleJsonExporter ex = new SimpleJsonExporter();
		element.accept(ex);
		return ex.writer.getObject();		
	}
	
	private void visitItem(Item item) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, item.getKey());
		Iterator<Designation> i = item.getDesignations().iterator();
		if (i.hasNext()) {
			writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, i.next().getSign());
		}
	}

	@Override
	public void visit(Assertion assertion) {
		visitItem(assertion);
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, assertionEnd.getKey());
	}

	@Override
	public void visit(RelationRole relationRole) {
		visit((Concept) relationRole);
	}

	@Override
	public void visit(Concept concept) {
		visitItem(concept);
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		visitItem(conceptSystem);
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		visit((Concept) conceptualDomain);
	}

	@Override
	public void visit(Context context) {
		visitItem(context);
	}

	@Override
	public void visit(DataElement dataElement) {
		visitItem(dataElement);
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		visit((Concept) dataElementConcept);
	}

	@Override
	public void visit(DataType dataType) {
		visitItem(dataType);
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, dataType.getName());
	}

	@Override
	public void visit(Definition definition) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, definition.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, definition.getText());
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
	}

	@Override
	public void visit(Designation designation) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, designation.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, designation.getSign());
	}

	@Override
	public void visit(DesignationContext designationContext) {
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		visit((Concept) dimensionality);
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		visitItem(languageIdentification);
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME,
				languageIdentification.getLanguageIdentifier());
	}

	@Override
	public void visit(Link link) {
		visit((Assertion) link);
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		visit((AssertionEnd) linkEnd);
	}

	@Override
	public void visit(Namespace namespace) {
		visitItem(namespace);
	}

	@Override
	public void visit(ObjectClass objectClass) {
		visit((Concept) objectClass);
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, permissibleValue.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, permissibleValue.getPermittedValue());
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		visitItem(referenceDocument);
	}

	@Override
	public void visit(Relation relation) {
		visit((Concept)relation);
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, scopedIdentifier.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, scopedIdentifier.getIdentifier());
	}

	@Override
	public void visit(Slot slot) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, slot.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, slot.getKey());
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		visit((Concept)unitOfMeasure);
	}

	@Override
	public void visit(ValueDomain valueDomain) {
		visitItem(valueDomain);
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		visit((Concept)valueMeaning);
	}

	@Override
	public void visit(User subject) {
		visitItem(subject);
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
