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

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
import net.xclinical.iso11179.ext.Visitable;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

/**
 * Exports XML content in generic MDX format.
 * 
 * @author ms@xclinical.com
 */
public class GenericXmlExporter extends AbstractVisitor implements Decls {

	private XMLStreamWriter writer;

	private HashSet<Key> ids = new HashSet<Key>();

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public GenericXmlExporter(OutputStream stm) throws XMLStreamException {
		XMLOutputFactory fac = XMLOutputFactory.newFactory();
		fac.setProperty("javax.xml.stream.isRepairingNamespaces", true);
		writer = new IndentingXMLStreamWriter(fac.createXMLStreamWriter(stm, "utf-8"));
		writer.writeStartDocument("utf-8", "1.0");
		writeStartElement(MDR);
	}

	public void close() throws XMLStreamException {
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
	}

	public void write(Item item) throws XMLStreamException {
		accept(item.getKey().getSimpleName(), item);
	}

	private boolean startItem(String name, HasKey item) throws XMLStreamException {
		Key key = item.getKey();
		if (ids.add(key)) {
			writeStartElement(name);
			writeAttribute(ID, key.toString());

			return true;
		} else {
			writeReference(name, item);
			return false;
		}
	}
	
	private void accept(String name, HasKey v) throws XMLStreamException {
		if (v != null) {
			if (startItem(name, v)) {
				((Visitable) v).accept(this);
				writer.writeEndElement();
			}
		}
	}

	private void writeStartElement(String localName) throws XMLStreamException {
		writer.writeStartElement(PREFIX, localName, MDR_NS);		
	}
	
	private void writeAttribute(String localName, String value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(MDR_NS, localName, value);
		}
	}

	private void writeAttribute(String localName, Boolean value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(MDR_NS, localName, value.toString());
		}
	}

	private void writeAttribute(String localName, Date value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(MDR_NS, localName, sdf.format(value));
		}
	}

	private void writeAttribute(String localName, Integer value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(MDR_NS, localName, String.valueOf(value));
		}
	}

	private void writeReference(String name, HasKey key) throws XMLStreamException {
		Key k = key.getKey();
		writer.writeEmptyElement(PREFIX, name, MDR_NS);
		writeAttribute(IDREF, k.toString());
	}

	private void writeChildren(String localName, Collection<? extends Visitable> items) throws XMLStreamException {
		if (items != null && items.size() > 0) {
			writeStartElement(localName);
			for (Visitable v : items) {
				if (v instanceof HasKey) {
					accept("item", (HasKey)v);
				}
				else {
					// Simple elements must write their enclosing element by themselves.
					v.accept(this);
				}
			}
			writer.writeEndElement();
		}
	}

	private abstract class HasKeyXml<T extends HasKey> {
		public void write(T obj) throws ExportException {
			try {
				writeAttributes(obj);
				writeElements(obj);
			} catch (XMLStreamException e) {
				throw new ExportException(e);
			}
		}

		public void writeAttributes(T obj) throws XMLStreamException {
		}

		public void writeElements(T obj) throws XMLStreamException {
		}
	}

	private abstract class ItemXml<T extends Item> extends HasKeyXml<T> {
		public void writeElements(T obj) throws XMLStreamException {
			writeChildren("designations", obj.getDesignations());
			writeChildren("definitions", obj.getDefinitions());
		};
	}

	private class ContextXml extends ItemXml<Context> {
		@Override
		public void writeElements(Context obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("relevantDesignations", obj.getRelevantDesignations());
			writeChildren("relevantDefinitions", obj.getRelevantDefinitions());
		}
	}

	@Override
	public void visit(Context context) {
		new ContextXml().write(context);
	}

	private class DesignationXml extends HasKeyXml<Designation> {
		@Override
		public void writeElements(Designation obj) throws XMLStreamException {
			super.writeElements(obj);
			writeStartElement("sign");
			writer.writeCData(obj.getSign());
			writer.writeEndElement();
			accept("item", obj.getItem());
			accept("language", obj.getLanguage());
			writeChildren("scopes", obj.getScopes());
		}
	}

	@Override
	public void visit(Designation designation) {
		new DesignationXml().write(designation);
	}

	private class DefinitionXml extends HasKeyXml<Definition> {
		@Override
		public void writeAttributes(Definition obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("text", obj.getText());
		}

		@Override
		public void writeElements(Definition obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("sourceReference", obj.getSourceReference());
			accept("item", obj.getItem());
			accept("language", obj.getLanguage());
			writeChildren("Scopes", obj.getScopes());
		}
	}

	@Override
	public void visit(Definition definition) {
		new DefinitionXml().write(definition);
	}

	@Override
	public void visit(DesignationContext designationContext) {
		try {
			writeStartElement("DesignationContext");
			accept("context", designationContext.getContext());
			accept("designation", designationContext.getDesignation());
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new ExportException(e);
		}
	}

	@Override
	public void visit(DefinitionContext definitionContext) {
		try {
			writeStartElement("DefinitionContext");
			accept("context", definitionContext.getContext());
			accept("definition", definitionContext.getDefinition());
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new ExportException(e);
		}
	}

	public class AssertionXml<T extends Assertion> extends ItemXml<T> {
		@Override
		public void writeAttributes(T obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("formula", obj.getFormula());
		}

		@Override
		public void writeElements(T obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("assertor", obj.getAssertor());
			writeChildren("ScopedIdentifiers", obj.getScopedIdentifiers());
			writeChildren("Terms", obj.getTerms());
		}
	}

	@Override
	public void visit(Assertion assertion) {
		new AssertionXml<Assertion>().write(assertion);
	}

	public class AssertionEndXml<T extends AssertionEnd> extends HasKeyXml<T> {
		@Override
		public void writeElements(T obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("end", obj.getEnd());
			accept("assertion", obj.getAssertion());
		}
	}

	@Override
	public void visit(AssertionEnd assertionEnd) {
		new AssertionEndXml<AssertionEnd>().write(assertionEnd);
	}

	public class ConceptXml<T extends Concept> extends ItemXml<T> {
		@Override
		public void writeElements(T obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("including", obj.getIncluding());
			writeChildren("assertions", obj.getAssertions());
		}
	}

	@Override
	public void visit(Concept concept) {
		new ConceptXml<Concept>().write(concept);
	}

	public class ConceptSystemXml extends ItemXml<ConceptSystem> {
		@Override
		public void writeElements(ConceptSystem obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("includedAssertions", obj.getIncludedAssertions());
			writeChildren("members", obj.getMembers());
		}
	}

	@Override
	public void visit(ConceptSystem conceptSystem) {
		new ConceptSystemXml().write(conceptSystem);
	}

	public class ConceptualDomainXml extends ConceptXml<ConceptualDomain> {
		@Override
		public void writeAttributes(ConceptualDomain obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("description", obj.getDescription());
		}

		@Override
		public void writeElements(ConceptualDomain obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("representations", obj.getRepresentations());
			writeChildren("usages", obj.getUsages());
			writeChildren("members", obj.getMembers());
		}
	}

	@Override
	public void visit(ConceptualDomain conceptualDomain) {
		new ConceptualDomainXml().write(conceptualDomain);
	}

	public class DataElementXml extends ItemXml<DataElement> {
		@Override
		public void writeAttributes(DataElement obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("precision", String.valueOf(obj.getPrecision()));
		}

		@Override
		public void writeElements(DataElement obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("domain", obj.getDomain());
			accept("meaning", obj.getMeaning());
		}
	}

	@Override
	public void visit(DataElement dataElement) {
		new DataElementXml().write(dataElement);
	}

	public class DataElementConceptXml extends ConceptXml<DataElementConcept> {
		@Override
		public void writeElements(DataElementConcept obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("objectClass", obj.getObjectClass());
			accept("characteristic", obj.getCharacteristic());
			writeChildren("domains", obj.getConceptualDomains());
			writeChildren("representations", obj.getRepresentations());
		}
	}

	@Override
	public void visit(DataElementConcept dataElementConcept) {
		new DataElementConceptXml().write(dataElementConcept);
	}

	public class DataTypeXml extends ItemXml<DataType> {
		@Override
		public void writeAttributes(DataType obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("name", obj.getName());
			writeAttribute("description", obj.getDescription());
			writeAttribute("schemeReference", obj.getSchemeReference());
		}
	}

	@Override
	public void visit(DataType dataType) {
		new DataTypeXml().write(dataType);
	}

	public class DimensionalityXml extends ConceptXml<Dimensionality> {
		@Override
		public void writeAttributes(Dimensionality obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("coordinateIndicator", obj.isCoordinateIndicator());
		}

		@Override
		public void writeElements(Dimensionality obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("applicableUnits", obj.getApplicableUnits());
		}
	}

	@Override
	public void visit(Dimensionality dimensionality) {
		new DimensionalityXml().write(dimensionality);
	}

	public class LanguageIdentificationXml extends HasKeyXml<LanguageIdentification> {
		@Override
		public void writeAttributes(LanguageIdentification obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("languageIdentifier", obj.getLanguageIdentifier());
		}
	}

	@Override
	public void visit(LanguageIdentification languageIdentification) {
		new LanguageIdentificationXml().write(languageIdentification);
	}

	public class LinkXml extends AssertionXml<Link> {
		@Override
		public void writeElements(Link obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("relation", obj.getRelation());
		}
	}

	@Override
	public void visit(Link link) {
		new LinkXml().write(link);
	}

	public class LinkEndXml extends AssertionEndXml<LinkEnd> {
		@Override
		public void writeElements(LinkEnd obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("EndRoles", obj.getEndRoles());
		}
	}

	@Override
	public void visit(LinkEnd linkEnd) {
		new LinkEndXml().write(linkEnd);
	}

	public class NamespaceXml extends HasKeyXml<Namespace> {
		@Override
		public void writeAttributes(Namespace obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("oneNamePerItem", obj.getOneNamePerItem());
			writeAttribute("oneItemPerName", obj.getOneItemPerName());
			writeAttribute("mandatoryNamingConvention", obj.getMandatoryNamingConvention());
			writeAttribute("shortHandPrefix", obj.getShorthandPrefix());
			writeAttribute("namespaceSchemeReference", obj.getNamespaceSchemeReference());
		}
	}

	@Override
	public void visit(Namespace namespace) {
		new NamespaceXml().write(namespace);
	}

	public class ObjectClassXml extends ConceptXml<ObjectClass> {
		@Override
		public void writeElements(ObjectClass obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("Concepts", obj.getConcepts());
		}
	}

	@Override
	public void visit(ObjectClass objectClass) {
		new ObjectClassXml().write(objectClass);
	}

	public class PermissibleValueXml extends HasKeyXml<PermissibleValue> {
		@Override
		public void writeAttributes(PermissibleValue obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("permittedValue", obj.getPermittedValue());
			writeAttribute("begin", obj.getBegin());
			writeAttribute("end", obj.getEnd());
		}

		@Override
		public void writeElements(PermissibleValue obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("meaning", obj.getMeaning());
			writeChildren("containingDomains", obj.getContainingDomains());
		}
	}

	@Override
	public void visit(PermissibleValue permissibleValue) {
		new PermissibleValueXml().write(permissibleValue);
	}

	public class ReferenceDocumentXml extends ItemXml<ReferenceDocument> {
		@Override
		public void writeAttributes(ReferenceDocument obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("identifier", obj.getIdentifier());
			writeAttribute("typeDescription", obj.getTypeDescription());
			writeAttribute("title", obj.getTitle());
			writeAttribute("uri", obj.getUri());
		}

		@Override
		public void writeElements(ReferenceDocument obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("language", obj.getLanguage());
		};
	}

	@Override
	public void visit(ReferenceDocument referenceDocument) {
		new ReferenceDocumentXml().write(referenceDocument);
	}

	public class RelationXml extends ConceptXml<Relation> {
		@Override
		public void writeElements(Relation obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("roles", obj.getRoles());
			writeChildren("links", obj.getLinks());
		}
	}

	@Override
	public void visit(Relation relation) {
		new RelationXml().write(relation);
	}

	public class RelationRoleXml extends ConceptXml<RelationRole> {
		@Override
		public void writeElements(RelationRole obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("source", obj.getSource());
			writeChildren("linkEnds", obj.getLinkEnds());
		}
	}

	@Override
	public void visit(RelationRole relationRole) {
		new RelationRoleXml().write(relationRole);
	}

	public class ScopedIdentifierXml extends HasKeyXml<ScopedIdentifier> {
		@Override
		public void writeAttributes(ScopedIdentifier obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("identifier", obj.getIdentifier());
			writeAttribute("fullExpansion", obj.getFullExpansion());
			writeAttribute("shorthandExpansion", obj.getShorthandExpansion());
		}

		@Override
		public void writeElements(ScopedIdentifier obj) throws XMLStreamException {
			super.writeElements(obj);
			accept("item", obj.getItem());
		}
	}

	@Override
	public void visit(ScopedIdentifier scopedIdentifier) {
		new ScopedIdentifierXml().write(scopedIdentifier);
	}

	public class SlotXml extends HasKeyXml<Slot> {
		@Override
		public void writeAttributes(Slot obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("name", obj.getName());
			writeAttribute("type", obj.getType());
			writeAttribute("value", obj.getValue());
		}
	}

	@Override
	public void visit(Slot slot) {
		new SlotXml().write(slot);
	}

	public class UnitOfMeasureXml extends ConceptXml<UnitOfMeasure> {
		@Override
		public void writeElements(UnitOfMeasure obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("dimensionalities", obj.getDimensionalities());
		}
	}

	@Override
	public void visit(UnitOfMeasure unitOfMeasure) {
		new UnitOfMeasureXml().write(unitOfMeasure);
	}

	public class ValueDomainXml extends ItemXml<ValueDomain> {
		@Override
		public void writeAttributes(ValueDomain obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("maximumCharacterQuantity", obj.getMaximumCharacterQuantity());
		}

		@Override
		public void writeElements(ValueDomain obj) throws XMLStreamException {
			super.writeElements(obj);
			writeAttribute("maximumCharacterQuantity", obj.getMaximumCharacterQuantity());
		}
	}

	@Override
	public void visit(ValueDomain valueDomain) {
		new ValueDomainXml().write(valueDomain);
	}

	public class ValueMeaningXml extends ConceptXml<ValueMeaning> {
		@Override
		public void writeAttributes(ValueMeaning obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("begin", obj.getBegin());
			writeAttribute("end", obj.getEnd());
		}

		@Override
		public void writeElements(ValueMeaning obj) throws XMLStreamException {
			super.writeElements(obj);
			writeChildren("representations", obj.getRepresentations());
		}
	}

	@Override
	public void visit(ValueMeaning valueMeaning) {
		new ValueMeaningXml().write(valueMeaning);
	}

	public class SubjectXml extends ItemXml<User> {
		@Override
		public void writeAttributes(User obj) throws XMLStreamException {
			super.writeAttributes(obj);
			writeAttribute("email", obj.getEmail());
		}
	}

	@Override
	public void visit(User subject) {
		new SubjectXml().write(subject);
	}

	@Override
	public void visit(Rating rating) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(LoginInfo loginInfo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ResultList resultList) {
		throw new UnsupportedOperationException();
	}
}
