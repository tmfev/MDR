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
package com.xclinical.mdr.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xclinical.iso11179.ext.Visitable;

import org.json.JSONException;

import com.google.inject.Singleton;
import com.mictale.jsonite.JsonValue;
import com.mictale.jsonite.stream.JsonValueProducer;
import com.mictale.jsonite.stream.Transformation;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.JsonCommandServlet;
import com.xclinical.mdr.server.util.Logger;

@Singleton
public class RestfulEntityServlet extends HttpServlet {

	private static final long serialVersionUID = 7013973685993734029L;

	private static final Logger LOG = Logger.get(RestfulEntityServlet.class);
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
				
		LOG.info("Processing request");

		try {
			PMF.run("prod", new Callable<Void>() {
				@Override
				public Void call() throws Exception {
		
					final String[] path = req.getPathInfo().substring(1).split("/");
					
					final JsonValue result;
					
					if (path.length == 1) {
						Key key = Key.parse(path[0]);						
						Object requestedObject = PMF.find(key);
						
						LOG.info("Found {0} for key {1}", requestedObject, key);
						
						result = FlatJsonExporter.of((Visitable)requestedObject);						
					}
					else if (path.length == 2) {
						Key key = Key.parse(path[0]);						
						Object requestedObject = PMF.find(key);

						final String name = path[1];
						
						LOG.info("Building {0} for key {1}", name, key);
						
						result = ListJsonExporter.of((Visitable)requestedObject, name);
					}
					else {
						throw new IllegalArgumentException("Unrecognized request");
					}
					
					resp.setStatus(HttpServletResponse.SC_OK);	
					PrintWriter w = resp.getWriter();
					Transformation.copy(w, true, new JsonValueProducer(result));
					w.println();
					w.println();
					
					return null;
				}
			});
		} catch (InvocationTargetException e) {
			LOG.severe(e);
			
			try {
				Throwable target = e.getTargetException();
				JsonCommandServlet.writeError((HttpServletResponse)resp, target.getMessage(), target.toString());
			} catch (JSONException e1) {
				LOG.severe(e1);
			}
		}		
	}
}
