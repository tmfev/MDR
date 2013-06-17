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

import java.util.ArrayList;
import java.util.Iterator;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.tree.JsonTreeWriter;
import com.xclinical.mdr.server.tree.TreeException;
import com.xclinical.mdr.server.tree.TreeWriter;

public class FlatJsonExporter extends AbstractVisitor {

	private JsonTreeWriter writer;

	private FlatJsonExporter() {
		this(new JsonTreeWriter());
	}

	private FlatJsonExporter(JsonTreeWriter writer) {
		this.writer = writer;
	}
	
	public static JSONArray of(Iterable<? extends Visitable> elements) {
		JSONArray a = new JSONArray();
		for (Visitable v : elements) {
			a.put(of(v));
		}
		
		return a;
	}

	public static void copy(TreeWriter writer, Visitable element) {
		FlatJsonExporter ex = new FlatJsonExporter((JsonTreeWriter)writer);
		element.accept(ex);		
	}
	
	public static JSONObject of(Visitable element) {
		FlatJsonExporter ex = new FlatJsonExporter();
		element.accept(ex);
		return ex.writer.getObject();		
	}
	
	private void visitItem(Item item) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, item.getKey());
		Iterator<Designation> i = item.getDesignations().iterator();
		if (i.hasNext()) {
			writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, i.next().getSign());
		}

		try {
			writer.getObject().put("rating", of(Rating.getAverageRating(item.getKey())));
		} catch (JSONException e) {
			throw new TreeException(e);
		}
	}

	@Override
	public void visit(Assertion assertion) {
		visitItem(assertion);
		writer.put("formula", assertion.getFormula());
		writer.put("assertor", assertion.getAssertor());
		writer.put("formula", assertion.getFormula());
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, assertionEnd.getKey());
		writer.put("end", assertionEnd.getEnd());
		writer.put("assertion", assertionEnd.getAssertion());
	}

	@Override
	public void visit(RelationRole relationRole) {
		visit((Concept) relationRole);
		writer.put("source", relationRole.getSource());
	}

	@Override
	public void visit(Concept concept) {
		visitItem(concept);
		writer.put("including", concept.getIncluding());
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		visitItem(conceptSystem);
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		visit((Concept) conceptualDomain);

		writer.put("description", conceptualDomain.getDescription());
	}

	@Override
	public void visit(Context context) {
		visitItem(context);
	}

	@Override
	public void visit(DataElement dataElement) {
		visitItem(dataElement);
		writer.put("precision", dataElement.getPrecision());
		writer.put("domain", dataElement.getDomain());
		writer.put("meaning", dataElement.getMeaning());
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		visit((Concept) dataElementConcept);

		writer.put("objectClass", dataElementConcept.getObjectClass());
		writer.put("characteristic", dataElementConcept.getCharacteristic());
	}

	@Override
	public void visit(DataType dataType) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, dataType.getName());
		visitItem(dataType);
		writer.put("name", dataType.getName());
		writer.put("description", dataType.getDescription());
		writer.put("schemeReference", dataType.getSchemeReference());
	}

	@Override
	public void visit(Definition definition) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, definition.getKey());
		writer.put("text", definition.getText());
		writer.put("displayName", definition.getText());
		writer.put("language", (HasKey)definition.getLanguage());
		writer.put("item", definition.getItem());
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
		writer.put("definition", definitionContext.getDefinition());
		writer.put("context", definitionContext.getContext());
	}

	@Override
	public void visit(Designation designation) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, designation.getKey());
		writer.put("displayName", designation.getSign());
		writer.put("sign", designation.getSign());
		writer.put("language", (HasKey)designation.getLanguage());
		writer.put("item", designation.getItem());
	}

	@Override
	public void visit(DesignationContext designationContext) {
		writer.put("designation", designationContext.getDesignation());
		writer.put("context", designationContext.getContext());
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		visit((Concept) dimensionality);
		writer.put("coordinateIndicator", dimensionality.isCoordinateIndicator());
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		visitItem(languageIdentification);
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME,
				languageIdentification.getLanguageIdentifier());
		writer.put("languageIdentifier", languageIdentification.getLanguageIdentifier());
	}

	@Override
	public void visit(Link link) {
		visit((Assertion) link);
		writer.put("relation", link.getRelation());
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		visit((AssertionEnd) linkEnd);
	}

	@Override
	public void visit(Namespace namespace) {
		visitItem(namespace);

		writer.put("oneNamePerItem", namespace.getOneNamePerItem());
		writer.put("oneItemPerName", namespace.getOneItemPerName());
		writer.put("mandatoryNamingConvention", namespace.getMandatoryNamingConvention());
		writer.put("shorthandPrefix", namespace.getShorthandPrefix());
		writer.put("namespaceSchemeReference", namespace.getNamespaceSchemeReference());
	}

	@Override
	public void visit(ObjectClass objectClass) {
		visit((Concept) objectClass);
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, permissibleValue.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, permissibleValue.getPermittedValue());
		writer.put("permittedValue", permissibleValue.getPermittedValue());
		writer.put("meaning", permissibleValue.getMeaning());
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		visitItem(referenceDocument);

		writer.put("identifier", referenceDocument.getIdentifier())
				.put("typeDescription", referenceDocument.getTypeDescription())
				.put("language", (HasKey)referenceDocument.getLanguage()).put("title", referenceDocument.getTitle())
				.put("uri", referenceDocument.getUri());

	}

	@Override
	public void visit(Relation relation) {
		visit((Concept)relation);
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, scopedIdentifier.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, scopedIdentifier.getIdentifier());
		writer.put("identifier", scopedIdentifier.getIdentifier());
		writer.put("fullExpansion", scopedIdentifier.getFullExpansion());
		writer.put("shorthandExpansion", scopedIdentifier.getShorthandExpansion());
	}

	@Override
	public void visit(Slot slot) {
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.ID, slot.getKey());
		writer.put(com.xclinical.mdr.client.iso11179.model.Item.DISPLAY_NAME, slot.getKey());
		writer.put("key", slot.getKey());
		writer.put("name", slot.getName());
		writer.put("type", slot.getType());
		writer.put("value", slot.getValue());
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		visit((Concept)unitOfMeasure);
	}

	@Override
	public void visit(ValueDomain valueDomain) {
		visitItem(valueDomain);
		
		writer.put("maximumCharacterQuantity", valueDomain.getMaximumCharacterQuantity());
		writer.put("dataType", valueDomain.getDataType());
		writer.put("format", valueDomain.getFormat());
		writer.put("meaning", valueDomain.getMeaning());
		writer.put("unitOfMeasure", valueDomain.getUnitOfMeasure());
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		visit((Concept)valueMeaning);
	}

	@Override
	public void visit(Rating rating) {
		writer.put("id", com.xclinical.mdr.client.iso11179.model.Rating.URN);
		writer.put("target", rating.getTarget());
		writer.put("averageRating", rating.getAverageValue());
		writer.put("votes", rating.getVotes());
		writer.put("rater", rating.getRater());
		writer.put("value", rating.getValue());
	}
	
	@Override
	public void visit(LoginInfo loginInfo) {
		writer.put("user", loginInfo.getUser());
		writer.put("session", loginInfo.getSession());
	}
	
	@Override
	public void visit(ResultList resultList) {
		writer.put(com.xclinical.mdr.client.iso11179.model.ResultList.START, resultList.getStart());
		writer.put(com.xclinical.mdr.client.iso11179.model.ResultList.LENGTH, resultList.getLength());
		
		List<Object> elements = resultList.getElements();
		List<Visitable> v = new ArrayList<Visitable>();

		for (Object o : elements) {
			if (o instanceof Visitable) {
				v.add((Visitable)o);
			}
			else if (o instanceof Object[]) {
				Visitable vo = (Visitable)((Object[])o)[0];
				v.add(vo);
			}	
			else if (o instanceof Key) {
				v.add((Visitable) PMF.find((Key)o));
			}
		}
		writer.put(com.xclinical.mdr.client.iso11179.model.ResultList.ELEMENTS, v);			
	}
	
	@Override
	public void visit(User subject) {
		visitItem(subject);
		writer.put("email", subject.getEmail());
	}
	
	@Override
	public void visit(UserGroup subjectGroup) {
		visitItem(subjectGroup);
	}
}
