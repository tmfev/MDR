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
package net.xclinical.iso11179.xml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

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
import net.xclinical.iso11179.Types;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;
import net.xclinical.iso11179.ext.AbstractVisitor;
import net.xclinical.iso11179.ext.LoginInfo;
import net.xclinical.iso11179.ext.Rating;
import net.xclinical.iso11179.ext.ResultList;
import net.xclinical.iso11179.ext.User;
import net.xclinical.iso11179.ext.Visitable;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

public class GenericXmlImporter extends AbstractVisitor implements Decls {

	private static final Logger log = Logger.get(GenericXmlImporter.class);

	private XMLStreamReader reader;

	private Map<Key, Key> keys = new HashMap<Key, Key>();

	private Stack<HasKey> entities = new Stack<HasKey>();

	public GenericXmlImporter(InputStream stm) throws XMLStreamException {
		XMLInputFactory fac = XMLInputFactory.newFactory();
		reader = fac.createXMLStreamReader(stm);
	}

	public void read() throws XMLStreamException {
		if (reader.nextTag() != XMLStreamReader.START_ELEMENT) {
			throw new IllegalArgumentException("Unexpected document format");
		}

		if (!MDR.equals(reader.getLocalName()) || !MDR_NS.equals(reader.getNamespaceURI())) {
			throw new IllegalArgumentException("Unexpected document element");
		}

		while (reader.next() != XMLStreamReader.END_DOCUMENT) {
			if (reader.getEventType() == XMLEvent.START_ELEMENT) {
				parseHasKey();
			}
		}

		if (reader.getEventType() != XMLEvent.END_DOCUMENT) {
			throw new IllegalArgumentException("Expected end of document");
		}
	}

	private HasKey parseHasKey() throws XMLStreamException {
		String idref = reader.getAttributeValue(MDR_NS, IDREF);
		if (idref != null) {
			reader.nextTag();
			if (reader.getEventType() != XMLEvent.END_ELEMENT) {
				throw new IllegalArgumentException("No end element " + reader.getLocation());
			}
			reader.nextTag();
			return (HasKey) PMF.find(keys.get(Key.parse(idref)));
		} else {
			String id = reader.getAttributeValue(MDR_NS, ID);
			if (id == null) {
				throw new IllegalArgumentException("No id or idref on element at " + reader.getLocation());
			}

			return (HasKey) parseDeclaration(Key.parse(id));
		}
	}

	private <T extends HasKey> T parseChild(Class<T> type) {
		Object r = null;
		try {
			r = parseHasKey();
			return type.cast(r);
		} catch (XMLStreamException e) {
			throw new IllegalArgumentException(e);
		} catch (ClassCastException e) {
			throw new ClassCastException("The object " + r + " could not be casted to " + type);
		}
	}

	private HasKey parseDeclaration(Key key) throws XMLStreamException {
		HasKey instance = Types.newInstance(key.getName());
		PMF.get().persist(instance);
		keys.put(key, instance.getKey());
		entities.push(instance);

		((Visitable) instance).accept(this);

		return instance;
	}

	public class DesignatableItemParser<T extends Item> extends ElementParser<Context> {
		public void onDefinitions(T obj) {
			new ElementParser<T>() {
				public void onItem(T obj) {
					obj.addDefinition(parseChild(Definition.class));
				}
			}.parseChildren(obj);
		}

		public void onDesignations(T obj) {
			new ElementParser<T>() {
				public void onItem(T obj) {
					obj.addDesignation(parseChild(Designation.class));
				}
			}.parseChildren(obj);
		}
	}

	public class AssertionParser<T extends Assertion> extends DesignatableItemParser<T> {
		public void onTerms(T obj) {
			new ElementParser<T>() {
				public void onItem(T obj) {
					obj.addTerm(parseChild(AssertionEnd.class));
				}
			}.parseChildren(obj);
		}

		public void onAssertor(T obj) {
			obj.setAssertor(parseChild(ConceptSystem.class));
		}
	}

	@Override
	public void visit(Assertion assertion) {
		new AssertionParser<Assertion>().parse(assertion);
	}

	public class AssertionEndParser<T extends AssertionEnd> extends ElementParser<T> {
		public void onEnd(T obj) {
			obj.setEnd(parseChild(Concept.class));
		}

		public void onAssertion(T obj) {
			obj.setAssertion(parseChild(Assertion.class));
		}
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
		new AssertionEndParser<AssertionEnd>().parse(assertionEnd);
	}

	public class ConceptParser<T extends Concept> extends DesignatableItemParser<T> {
		public void onIncluding(T obj) {
			obj.setIncluding(parseChild(ConceptSystem.class));
		}

		public void onAssertions(Concept obj) {
			new ElementParser<Concept>() {
				public void onItem(Concept obj) {
					obj.getAssertions().add(parseChild(AssertionEnd.class));
				}
			}.parseChildren(obj);
		}
	}

	@Override
	public void visit(Concept concept) {
		new ConceptParser<Concept>().parse(concept);
	}

	public class ConceptSystemParser extends DesignatableItemParser<RelationRole> {
		public void onIncludedAssertions(ConceptSystem obj) {
			new ElementParser<ConceptSystem>() {
				public void onItem(ConceptSystem obj) {
					obj.addIncludedAssertion(parseChild(Assertion.class));
				}
			}.parseChildren(obj);
		}

		public void onMembers(ConceptSystem obj) {
			new ElementParser<ConceptSystem>() {
				public void onItem(ConceptSystem obj) {
					obj.addMember(parseChild(Concept.class));
				}
			}.parseChildren(obj);
		}

	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		new ConceptSystemParser().parse(conceptSystem);
	}

	public class ConceptualDomainParser extends ConceptParser<ConceptualDomain> {

	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		new ConceptualDomainParser().parse(conceptualDomain);
	}

	public class ContextParser extends DesignatableItemParser<Context> {
		public void onRelevantDesignations(Context context) {
			new ElementParser<Context>() {
				public void onDesignationContext(Context obj) {
					DesignationContext c = new DesignationContext();
					c.accept(GenericXmlImporter.this);
					obj.addRelevantDesignation(DesignationContext.create(c.getDesignation(), c.getContext()));
				}
			}.parseChildren(context);
		}

		public void onRelevantDefinitions(Context context) {
			new ElementParser<Context>() {
				public void onDefinitionContext(Context obj) {
					DefinitionContext c = new DefinitionContext();
					c.accept(GenericXmlImporter.this);
					obj.addRelevantDefinition(DefinitionContext.create(c.getDefinition(), c.getContext()));
				}
			}.parseChildren(context);
		}
	}

	@Override
	public void visit(Context context) {
		new ContextParser().parse(context);
	}

	@Override
	public void visit(DataElement dataElement) {
		new FFParser().parse(dataElement);
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		new FFParser().parse(dataElementConcept);
	}

	@Override
	public void visit(DataType dataType) {
		new FFParser().parse(dataType);
	}

	public class DefinitionParser extends ElementParser<Definition> {

		@Override
		public void onAttributes(Definition obj) throws XMLStreamException {
			obj.setText(reader.getAttributeValue(MDR_NS, "text"));
		}

		public void onItem(Definition obj) {
			obj.setItem(parseChild(Item.class));
		}

		public void onScopes(Definition obj) {
			parseChildren(obj);
		}

		public void onDefinitionContext(Definition obj) {
			DefinitionContext c = new DefinitionContext();
			c.accept(GenericXmlImporter.this);
			obj.addScope(DefinitionContext.create(c.getDefinition(), c.getContext()));
		}
	}

	@Override
	public void visit(Definition definition) {
		new DefinitionParser().parse(definition);
	}

	public class DefinitionContextParser extends ElementParser<DefinitionContext> {
		public void onContext(DefinitionContext obj) {
			obj.setContext(parseChild(Context.class));
		}

		public void onDefinition(DefinitionContext obj) {
			obj.setDefinition(parseChild(Definition.class));
		}
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
		new DefinitionContextParser().parse(definitionContext);
	}

	public class DesignationParser extends ElementParser<Designation> {

		public void onSign(Designation obj) throws XMLStreamException {
			reader.next();
			obj.setSign(reader.getText());
			reader.nextTag();
			reader.nextTag();
		}

		public void onItem(Designation obj) {
			obj.setItem(parseChild(Item.class));
		}

		public void onLanguage(Designation obj) {
			obj.setLanguage(parseChild(LanguageIdentification.class));
		}

		public void onScopes(Designation obj) {
			parseChildren(obj);
		}

		public void onDesignationContext(Designation obj) {
			DesignationContext c = new DesignationContext();
			c.accept(GenericXmlImporter.this);
			obj.addScope(DesignationContext.create(c.getDesignation(), c.getContext()));
		}
	}

	@Override
	public void visit(Designation designation) {
		new DesignationParser().parse(designation);
	}

	/*
	public class DesignationContextParser extends ElementParser<DesignationContext> {
		public void onContext(DesignationContext obj) {
			obj.setContext(parseChild(Context.class));
		}

		public void onDesignation(DesignationContext obj) {
			obj.setDesignation(parseChild(Designation.class));
		}
	}
	*/

	@Override
	public void visit(DesignationContext designationContext) {
		throw new UnsupportedOperationException();
		// new DesignationContextParser().parse(designationContext);
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		new FFParser().parse(dimensionality);
	}

	public class LanguageIdentificationParser extends ElementParser<LanguageIdentification> {
		@Override
		public void onAttributes(LanguageIdentification obj) throws XMLStreamException {
			obj.setLanguageIdentifier(reader.getAttributeValue(MDR_NS, "languageIdentifier"));
		}
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		new LanguageIdentificationParser().parse(languageIdentification);
	}

	public class LinkParser extends AssertionParser<Link> {

	}

	@Override
	public void visit(Link link) {
		new FFParser().parse(link);
	}

	public class LinkEndParser extends AssertionEndParser<LinkEnd> {
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		new LinkEndParser().parse(linkEnd);
	}

	public class NamespaceParser extends DesignatableItemParser<Namespace> {

	}

	@Override
	public void visit(Namespace namespace) {
		new NamespaceParser().parse(namespace);
	}

	@Override
	public void visit(ObjectClass objectClass) {
		new FFParser().parse(objectClass);
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		new FFParser().parse(permissibleValue);
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		new FFParser().parse(referenceDocument);
	}

	public class RelationParser extends DesignatableItemParser<RelationRole> {

	}

	@Override
	public void visit(Relation relation) {
		new RelationParser().parse(relation);
	}

	public class RelationRoleParser extends DesignatableItemParser<RelationRole> {

	}

	@Override
	public void visit(RelationRole relationRole) {
		new RelationRoleParser().parse(relationRole);
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
		new FFParser().parse(scopedIdentifier);
	}

	@Override
	public void visit(Slot slot) {
		new FFParser().parse(slot);
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		new FFParser().parse(unitOfMeasure);
	}

	public class ValueDomainParser extends DesignatableItemParser<RelationRole> {

	}

	@Override
	public void visit(ValueDomain valueDomain) {
		new ValueDomainParser().parse(valueDomain);
	}

	public class ValueMeaningParser extends ConceptParser<RelationRole> {
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		new ValueMeaningParser().parse(valueMeaning);
	}

	@Override
	public void visit(User subject) {
		new FFParser().parse(subject);
	}

	private class FFParser extends ElementParser<Object> {
		public FFParser() {
		}

		@Override
		public void parse(Object obj) {
			fail("Replace FFParser for object: " + obj);
		}
	}

	@Override
	public void visit(Rating rating) {
		new FFParser().parse(rating);
	}

	@Override
	public void visit(LoginInfo loginInfo) {
		new FFParser().parse(loginInfo);
	}

	@Override
	public void visit(ResultList resultList) {
		new FFParser().parse(resultList);
	}

	protected boolean is(String name) {
		return name.equals(reader.getLocalName());
	}

	protected <T> void parseChildren(String name, ElementParser<T> parser, T obj) throws XMLStreamException {
		if (is(name)) {
			if (reader.nextTag() != XMLEvent.END_ELEMENT) {
				// Children.
				while (reader.getEventType() != XMLEvent.END_ELEMENT) {
					parser.onElement(obj);
				}
			}

			reader.nextTag();
		}
	}

	private class ElementParser<T> {
		public void log(String message) {
			log.info(message + ": " + reader.getLocation());
		}

		public void fail(String message) {
			throw new RuntimeException(message + ": " + reader.getLocation());
		}

		public void assertEventType(int eventType) {
			if (reader.getEventType() != eventType) {
				fail("Unexpected event type");
			}
		}

		public void parseChildren(Object obj) {
			assertEventType(XMLEvent.START_ELEMENT);
			try {
				if (reader.nextTag() != XMLEvent.END_ELEMENT) {
					// Children.
					while (reader.getEventType() != XMLEvent.END_ELEMENT) {
						onElement(obj);
					}
				}

				reader.nextTag();
			} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}
		}

		public void parse(Object obj) {
			try {
				onAttributes((T) obj);
				parseChildren(obj);
			} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}
		}

		public void onAttributes(T obj) throws XMLStreamException {
		}

		public final void onElement(Object obj) throws XMLStreamException {
			String name = reader.getLocalName();
			log.info("Element " + name + " in " + this);

			for (Class<?> clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
				try {
					Method m = getClass().getMethod("on" + Character.toUpperCase(name.charAt(0)) + name.substring(1),
							clazz);
					m.invoke(this, obj);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getTargetException());
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchMethodException e) {
				}
			}
		}
	}
}
