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
package com.xclinical.mdr.server.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public final class XmlUtils {

	public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
	
	private XmlUtils() {		
	}

	public static final void assertEventType(XMLStreamReader xml, int expected) throws XmlParseException {
		if (xml.getEventType() != expected) throw new XmlParseException("Expected " + getEventTypeString(expected) + ", found " + getEventTypeString(xml.getEventType()));	
	}

	public static final void assertStartDocument(XMLStreamReader xml) throws XmlParseException {
		assertEventType(xml, XMLEvent.START_DOCUMENT);
	}

	public static final void eatStartDocument(XMLStreamReader xml) throws XMLStreamException, XmlParseException {
		assertStartDocument(xml);
		xml.nextTag();
	}
	
	public static final void assertStartElement(XMLStreamReader xml) throws XmlParseException {
		assertEventType(xml, XMLEvent.START_ELEMENT);
	}

	public static final void assertStartElement(XMLStreamReader xml, String localName) throws XmlParseException {
		assertEventType(xml, XMLEvent.START_ELEMENT);
		assertLocalName(xml, localName);
	}

	public static final void assertStartElement(XMLStreamReader xml, String localName, String ns) throws XmlParseException {
		assertEventType(xml, XMLEvent.START_ELEMENT);
		assertLocalName(xml, localName);
		assertNamspace(xml, ns);
	}
	
	public static final void assertEndElement(XMLStreamReader xml) throws XmlParseException {
		assertEventType(xml, XMLEvent.END_ELEMENT);
	}

	public static final void assertEndElement(XMLStreamReader xml, String localName) throws XmlParseException {
		assertEventType(xml, XMLEvent.END_ELEMENT);
		assertLocalName(xml, localName);
	}

	public static final void assertLocalName(XMLStreamReader xml, String localName) throws XmlParseException {
		if (!localName.equals(xml.getLocalName())) throw new XmlParseException("Unexpected element " + xml.getLocalName() + ", expected " + localName);
	}

	public static final void assertNamspace(XMLStreamReader xml, String ns) throws XmlParseException {
		if (!ns.equals(xml.getNamespaceURI())) throw new XmlParseException("Unexpected namespace " + xml.getNamespaceURI() + ", expected " + ns);
	}
	
	public static final String getEventTypeString(int eventType) {
	    switch (eventType) {
	        case XMLEvent.START_ELEMENT:
	            return "START_ELEMENT";
	        case XMLEvent.END_ELEMENT:
	            return "END_ELEMENT";
	        case XMLEvent.PROCESSING_INSTRUCTION:
	            return "PROCESSING_INSTRUCTION";
	        case XMLEvent.CHARACTERS:
	            return "CHARACTERS";
	        case XMLEvent.COMMENT:
	            return "COMMENT";
	        case XMLEvent.START_DOCUMENT:
	            return "START_DOCUMENT";
	        case XMLEvent.END_DOCUMENT:
	            return "END_DOCUMENT";
	        case XMLEvent.ENTITY_REFERENCE:
	            return "ENTITY_REFERENCE";
	        case XMLEvent.ATTRIBUTE:
	            return "ATTRIBUTE";
	        case XMLEvent.DTD:
	            return "DTD";
	        case XMLEvent.CDATA:
	            return "CDATA";
	        case XMLEvent.SPACE:
	            return "SPACE";
	    }
	    return "UNKNOWN_EVENT_TYPE , " + eventType;
	}
}
