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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xclinical.iso11179.ext.SearchHelper;

import org.json.JSONException;

import com.google.inject.Singleton;
import com.mictale.jsonite.JsonValue;
import com.mictale.jsonite.stream.JsonValueProducer;
import com.mictale.jsonite.stream.Transformation;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.JsonCommandServlet;
import com.xclinical.mdr.server.util.Logger;

/**
 * Searches all items for a specified query term.
 * 
 * @author ms@xclinical.com
 */
@Singleton
public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 6871417631366972513L;
	
	private static final Logger LOG = Logger.get(QueryServlet.class);
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
				
		LOG.info("Processing request");

		try {
			PMF.run("prod", new Callable<Void>() {
				@Override
				public Void call() throws Exception {
		
					final String[] path = req.getPathInfo().substring(1).split("/");
					
					final String startStr = req.getParameter("start");
					final String lengthStr = req.getParameter("length");
					
					final String query = path[0];
					
					List<Object> items;
					List<Object[]> hits;

					SearchHelper search = new SearchHelper();
					
					final int start;
					if (startStr == null) {
						start = 0;
					}
					else {
						start = Integer.parseInt(startStr);
					}

					final int length;
					if (lengthStr == null) {
						length = 1000;
					}
					else {
						length = Integer.parseInt(lengthStr);
					}

					List<Object> params = new ArrayList<Object>();
					for (int i = 0; i < 100; i++) {
						String value = req.getParameter("p" + i);
						if (value == null) {
							break;
						}
						
						params.add(value);
					}
					
					hits = search.runNamedQuery(start, query, params.toArray());
					
					items = search.extractKey(length, hits);
							
					JsonValue list = FlatJsonExporter.of(items);
					
					resp.setStatus(HttpServletResponse.SC_OK);				
					resp.setContentType("application/json");		
					resp.setCharacterEncoding("UTF-8");
					OutputStream out = resp.getOutputStream();
					Writer w = new OutputStreamWriter(out, "UTF-8");

					Transformation.copy(w, true, new JsonValueProducer(list));
					w.write('\n');
					w.write('\n');

					w.flush();
					
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
