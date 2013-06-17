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
package net.xclinical.mdt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.xclinical.mdt.MDTParser.start_return;
import net.xclinical.mdt.ast.Block;
import net.xclinical.mdt.ast.ExecutionContext;
import net.xclinical.mdt.ast.Scope;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.xclinical.mdr.server.util.Logger;

public class MDTProcessor {

	private static final Logger log = Logger.get(MDTProcessor.class);
	
	public static final String NAMESPACE = "http://www.xclinical.net/ns/mdt";
	
	public static final String NAME = "name";
	public static final String VERSION = "version";
	public static final String AUTHOR = "author";
	public static final String EMAIL = "email";

	public MDTProcessor() {
	}

	/**
	 * Parses the meta data from an MDT file.
	 * 
	 * @param definition is the file to parse.
	 * @return a map of meta info.
	 */
	public Map<String, Object> getMetaInfo(Source definition) throws IOException {
		InputStream defStm = definition.resolve();
		Reader r = new InputStreamReader(defStm);
		try {
			MDTLexer lex = new MDTLexer(new ANTLRReaderStream(r));
			CommonTokenStream tokens = new CommonTokenStream(lex);
			MDTParser parser = new MDTParser(tokens);

			start_return root = parser.start();
			// WALK RESULTING TREE
			CommonTree t = (CommonTree) root.getTree(); // get tree from parser

			Hashtable<String, Object> props = new Hashtable<String, Object>();
			
			if (t != null) {
				Log.m(t.toStringTree());
	
				// Create a tree node stream from resulting tree
				CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
				MDTMetaParser walker = new MDTMetaParser(nodes); // create a tree
																	// parser
				walker.start(props); // launch at start rule prog
	
				Log.m(props.toString());
			}
			
			return props;
		} catch (RecognitionException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}		
	}
	
	/**
	 * Processes the content.
	 * 
	 * @param templateSource
	 * @param content
	 */
	public void process(Source templateSource, Source content) throws ProcessingException {
		try {
			InputStream templateStm = templateSource.resolve();
			if (templateStm == null) throw new ProcessingException("Failed to open template stream");
			Reader templateReader = new InputStreamReader(templateStm);

			Log.m("Dump of root rule");
			
			MDTLexer templateLexer = new MDTLexer(new ANTLRReaderStream(templateReader));
			CommonTokenStream templateTokenStream = new CommonTokenStream(templateLexer);
			MDTParser templateParser = new MDTParser(templateTokenStream);

			start_return root = templateParser.start();
			// WALK RESULTING TREE
			CommonTree t = (CommonTree) root.getTree(); // get tree from parser

			Log.m("Finished parsing template file:");
			String tree = t.toStringTree();
			log.debug(tree);
			Log.m(tree);

			// Create a tree node stream from resulting tree
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
			MDTTreeWalker walker = new MDTTreeWalker(nodes); // create a tree
																// parser
			
			Block rootBlock = new Block();
			walker.start(rootBlock); // launch at start rule prog

			Log.m("Dump of root rule");
			rootBlock.dump(Log.stm());

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setValidating(false);

			DocumentBuilder builder = domFactory.newDocumentBuilder();

			Document doc = builder.parse(content.resolve());
	
			ExecutionContext context = ExecutionContext.newContext(doc);
			rootBlock.executeChildren(new Scope(), context);
		} catch (RecognitionException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		} catch (ParserConfigurationException e) {
			throw new ProcessingException(e);
		} catch (SAXParseException e) {
			throw new ProcessingException("Failed to process publicId " + e.getPublicId() + " at " + e.getLineNumber() + "," + e.getColumnNumber() + ": " + e.getMessage(), e);
		} catch (SAXException e) {
			throw new ProcessingException(e);
		}
	}
}
