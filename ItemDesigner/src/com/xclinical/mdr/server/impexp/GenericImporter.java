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
package com.xclinical.mdr.server.impexp;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.xclinical.mdr.server.xml.XmlParseException;
import com.xclinical.mdr.server.xml.XmlUtils;

public class GenericImporter implements Importer {

	private static XMLInputFactory factory = XMLInputFactory.newFactory();
	
	private static final String NS = "http://mdr.xclinical.net/ns/import";
	
	public GenericImporter() {
	}
	
	@Override
	public void read(ResourceResolver resources, String uri) throws IOException {
		InputStream stm = resources.open("META-INF/import.xml");
		if (stm == null) {
			throw new IOException("This is not a MDR-X container");
		}
		
		try {
			XMLStreamReader xml = factory.createXMLStreamReader(stm);
			XmlUtils.assertStartDocument(xml);
			xml.nextTag();
			XmlUtils.assertStartElement(xml, "import", NS);
			xml.nextTag();		

			XmlUtils.assertStartElement(xml, "processor", NS);
			String clazz = xml.getAttributeValue("", "class");
			
			Class<?> processorType = Class.forName(clazz);
			Importer importer = (Importer)processorType.newInstance();
			importer.read(resources, uri);
			
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (XmlParseException e) {
			throw new IOException(e);
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} catch (InstantiationException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		}
		
	}
}
