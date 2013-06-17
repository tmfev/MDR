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
package com.xclinical.mdr.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Namespace;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.ScopedIdentifier;
import net.xclinical.iso11179.ext.Document;
import net.xclinical.iso11179.ext.FlatJsonExporter;
import net.xclinical.iso11179.ext.Session;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.inject.Singleton;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.command.JsonCommandServlet;
import com.xclinical.mdr.server.util.Logger;

/**
 * Loads and stores {@link Document}s.
 * 
 *  @author ms@xclinical.com
 */
@Singleton
public class DocumentServlet extends HttpServlet {

	private static final Logger log = Logger.get(DocumentServlet.class);

	private static final long serialVersionUID = 1L;

	private static class FileInfo {
		byte[] content = null;
		String contentName = null;
		String contentType = null;
	}

	protected Document fromPath(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null) {
			return null;
		}
		else {
			String idString = path.substring(1);
			Key key = Key.create(com.xclinical.mdr.client.iso11179.model.Document.URN, Long.parseLong(idString));
			Document document = (Document)PMF.find(key);
			return document;
		}		
	}
	
	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		PMF.runInServlet(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				DocumentServlet.super.service(req, resp);
				return null;
			}
		});
	}
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		Document document = fromPath(req);
		
		resp.setContentType(document.getType());
        // resp.setHeader("Content-Disposition", "attachment;filename=" + document.getName());

		OutputStream stm = resp.getOutputStream();
		byte[] content = document.getContent();
		
		stm.write(content);
	}
		
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
			if (ServletFileUpload.isMultipartContent(req)) {
				log.debug("detected multipart content");

				final FileInfo info = new FileInfo();

				ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());

				@SuppressWarnings("unchecked")
				List<FileItem> items = fileUpload.parseRequest(req);

				String session = null;
				
				for (Iterator<FileItem> i = items.iterator(); i.hasNext();) {
					log.debug("detected form field");

					FileItem item = (FileItem) i.next();

					if (item.isFormField()) {
						String fieldName = item.getFieldName();
						String fieldValue = item.getString();

						if (fieldName != null) {
							log.debug("{0}={1}", fieldName, fieldValue);
						} else {
							log.severe("fieldName may not be null");
						}
						
						if ("session".equals(fieldName)) {
							session = fieldValue;
						}
					} else {
						log.debug("detected content");

						info.contentName = item.getName();
						info.contentName = new File(info.contentName).getName();
						info.contentType = item.getContentType();
						info.content = item.get();

						log.debug("{0} bytes", info.content.length);
					}
				}

				if (info.content == null)
					throw new IllegalArgumentException("there is no content");
				if (info.contentType == null)
					throw new IllegalArgumentException("There is no content type");
				if (info.contentName == null)
					throw new IllegalArgumentException("There is no content name");

				Session.runInSession(session, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						Document document = Document.create(info.contentName, info.contentType, info.content);

						log.info("Created document " + document.getId());

						ReferenceDocument referenceDocument = saveFile(req, document);
						
						JsonCommandServlet.writeResponse(resp, FlatJsonExporter.of(referenceDocument));
						return null;
					}
				});
			}
			else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);				
			}

		} catch (FileUploadException e) {
			log.severe(e);
			throw new ServletException(e);
		} catch (Exception e) {
			log.severe(e);
			throw new ServletException(e);
		}
	}

	private ReferenceDocument saveFile(HttpServletRequest req, final Document document) throws IOException, ServletException {
		final Context root = Context.root();

		final String uri = req.getRequestURI();
				
		String name = document.getName();
		ReferenceDocument referenceDocument = ReferenceDocument.create(name, uri + "/" + document.getId());						
		LanguageIdentification lang = LanguageIdentification.findOrCreate(LanguageIdentification.EN_US);	
		root.designate(referenceDocument, name, lang);
		Namespace ns = Namespace.findOrCreate(Namespace.DATA);
		ScopedIdentifier identifier = ScopedIdentifier.create(ns, document.getKey().toString());
		referenceDocument.addScopedIdentifier(identifier);
		
		return referenceDocument;
	}	
}
