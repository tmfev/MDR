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
package com.xclinical.mdr.server.io.odm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xclinical.mdr.server.xml.XmlUtils;

public class OdmParser {

	private static final String STUDY_ELEMENT = "Study";

	private static final String ITEM_DEF_ELEMENT = "ItemDef";

	private static final String DESCRIPTION_ELEMENT = "Description";

	private static final String TRANSLATED_TEXT_ELEMENT = "TranslatedText";
	
	private final SAXParser parser;

	OdmParser(SAXParser parser) {
		this.parser = parser;
	}

	public void parse(InputStream stm, OdmEventHandler handler) throws IOException, SAXException {
		parser.parse(stm, new Handler(handler));
	}

	private class Handler extends DefaultHandler {
		private final OdmEventHandler handler;

		private Stack<ParseAdapter> stack = new Stack<ParseAdapter>();
		
		private StringBuilder characters = new StringBuilder();
		
		public Handler(OdmEventHandler handler) {
			this.handler = handler;
		}

		private void start(Attributes attributes, ParseAdapter adapter) {
			adapter.start(attributes);
			stack.push(adapter);			
		}
		
		private void end() {
			ParseAdapter last = stack.pop();
			last.end();
			stack.peek().merge(last);
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (ODM.XML_NAMESPACE.equals(uri)) {
				if (STUDY_ELEMENT.equals(localName)) {
					start(attributes, new ParseAdapter(new Study()) {
						@Override
						public void start(Attributes attributes) {
							get(Study.class).setOid(attributes.getValue(OdmElement.OID_ATTRIBUTE));
						}
					});
					handler.study(stack.peek().get(Study.class));
				}
				else if (ITEM_DEF_ELEMENT.equals(localName)) {
					start(attributes, new ParseAdapter(new ItemDef()) {
						@Override
						public void start(Attributes attributes) {
							get(ItemDef.class).setOid(attributes.getValue(OdmElement.OID_ATTRIBUTE));
							get(ItemDef.class).setName(attributes.getValue(ItemDef.NAME_ATTRIBUTE));
							get(ItemDef.class).setDataType(DataType.fromName(attributes.getValue(ItemDef.DATA_TYPE_ATTRIBUTE)));
						}
						
						@Override
						public void merge(ParseAdapter child) {
							if (child.is(TranslatedText.class)) {
								get(ItemDef.class).addText(child.get(TranslatedText.class));
							}
						}
					});
				}
				else if (DESCRIPTION_ELEMENT.equals(localName)) {
					stack.push(stack.peek());
				}
				else if (TRANSLATED_TEXT_ELEMENT.equals(localName)) {
					start(attributes, new ParseAdapter(new TranslatedText()) {
						@Override
						public void start(Attributes attributes) {
							get(TranslatedText.class).setLang(attributes.getValue(XmlUtils.XMLNS, "lang"));
							super.start(attributes);
						}
						
						@Override
						public void end() {
							get(TranslatedText.class).setText(getText());
						}
					});
				}
				else {
					start(attributes, new ParseAdapter(null));					
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			stack.peek().characters(ch, start, length);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (ODM.XML_NAMESPACE.equals(uri)) {
				if (STUDY_ELEMENT.equals(localName)) {
				}
				else if (ITEM_DEF_ELEMENT.equals(localName)) {
					handler.itemDef(stack.peek().get(ItemDef.class));
				}
				else if (DESCRIPTION_ELEMENT.equals(localName)) {
				}
				else if (TRANSLATED_TEXT_ELEMENT.equals(localName)) {
				}
				
				try {
					end();
				}
				catch(Exception e) {					
				}
			}
		}
	}
}
