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

import java.util.Hashtable;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XmlRow {

	private final Map<String, String> fields = new Hashtable<String, String>();
	
	private XmlRow() {		
	}
	
	public static XmlRow fromXml(String elementName, XMLStreamReader xml) throws XMLStreamException, XmlParseException {
		XmlRow table = new XmlRow();
		
        XmlUtils.assertStartElement(xml);
        XmlUtils.assertLocalName(xml, elementName);
       
        while (xml.hasNext()) {
            int eventType = xml.next();
            switch(eventType) {
            case XMLEvent.END_ELEMENT:
            	XmlUtils.assertEndElement(xml, elementName);
            	xml.nextTag();
            	return table;

            case XMLEvent.START_ELEMENT:
            	String name = xml.getLocalName();
            	String value = xml.getElementText();
            	table.fields.put(name, value);
            	XmlUtils.assertEndElement(xml, name);
            	break;
            }
        }
        
        return table;
	}
	
	public String get(String name) {
		return fields.get(name);
	}

	public long getLong(String name) {
		return Long.parseLong(fields.get(name));
	}
}
