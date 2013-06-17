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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XmlTable {

	private static XMLInputFactory factory = XMLInputFactory.newFactory();

	private final XMLStreamReader xml;
	
	private final String rootTag;

	public XmlTable(InputStream stm, String rootTag) throws FileNotFoundException, XMLStreamException, XmlParseException {
		this.rootTag = rootTag;
		
		this.xml = factory.createXMLStreamReader(stm);
		
		XmlUtils.assertStartDocument(xml);
		xml.nextTag();
		XmlUtils.assertStartElement(xml, rootTag);
		xml.nextTag();		
	}
	
	public XmlTable(File file, String rootTag) throws FileNotFoundException, XMLStreamException, XmlParseException {
		this(new FileInputStream(file), rootTag);
	}

	public XmlRow nextRow(String elementName) throws XMLStreamException, XmlParseException {
		if (xml.getEventType() == XMLEvent.END_ELEMENT && rootTag.equals(xml.getLocalName())) return null;
		return XmlRow.fromXml(elementName, xml);
	}

	public XmlRow nextRow() throws XMLStreamException, XmlParseException {
		if (xml.getEventType() == XMLEvent.END_ELEMENT && rootTag.equals(xml.getLocalName())) return null;
		return XmlRow.fromXml(xml.getLocalName(), xml);
	}
}
