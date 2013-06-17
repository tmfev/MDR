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

import java.util.Map;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Namespace;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.ScopedIdentifier;
import net.xclinical.iso11179.ext.Document;
import net.xclinical.mdt.ByteArraySource;
import net.xclinical.mdt.Log;
import net.xclinical.mdt.MDTProcessor;
import net.xclinical.mdt.Source;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

public class Imports {

	private static final Logger log = Logger.get(Imports.class);

	public static void doImports(Source format, Source document) {	
		try {
			final MDTProcessor p = new MDTProcessor();
			
			final Map<String, Object> props = p.getMetaInfo(format);
			log.debug(props.toString());
	
			if (props.size() == 0) {
				throw new IllegalArgumentException("This is not a valid MDX format file");
			}
			
			p.process(format, document);
		}
		catch(Exception e) {
			Log.e(e);
		}
	}
	
	public static ReferenceDocument doImports(ReferenceDocument document, ReferenceDocument format) {	
		Namespace dataNs = Namespace.find(Namespace.DATA);
		final ScopedIdentifier dataId = document.getScopedIdentifier(dataNs);
		final Document doc = (Document)PMF.find(Key.parse(dataId.getIdentifier()));
		final ScopedIdentifier fmtId = format.getScopedIdentifier(dataNs);		
		final Document fmt = (Document)PMF.find(Key.parse(fmtId.getIdentifier()));
		
		doImports(new ByteArraySource(fmt.getContent()), new ByteArraySource(doc.getContent()));
		
		final Context root = Context.root();
		Document log = Document.create("Import Log", "text/plain", Log.toByteArray());
		ReferenceDocument logDocument = ReferenceDocument.create("Import Log", "/mdr/document/" + log.getId());
		LanguageIdentification lang = LanguageIdentification.findOrCreate(LanguageIdentification.EN_US);	
		root.designate(logDocument, "Import Log", lang);
		Namespace ns = Namespace.findOrCreate(Namespace.DATA);
		ScopedIdentifier identifier = ScopedIdentifier.create(ns, log.getKey().toString());
		logDocument.addScopedIdentifier(identifier);

		return logDocument;
		
		/*
		CdiscOdmImporter importer = new CdiscOdmImporter();
		importer.read(new SimpleResourceResolver(document.getContent()), uri + "/" + document.getId());
		*/
		// InputStream stm = new ByteArrayInputStream(document.getContent());
		// GenericImporter importer = new GenericImporter();
		// importer.read(new ZipResourceResolver(stm), uri + "/" + document.getId());

	}
}
