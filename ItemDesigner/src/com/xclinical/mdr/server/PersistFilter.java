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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.xclinical.iso11179.ext.Session;

import org.json.JSONException;

import com.google.inject.Singleton;
import com.xclinical.mdr.server.command.JsonCommandServlet;
import com.xclinical.mdr.server.util.Logger;

@Singleton
public class PersistFilter implements Filter {

	private static final Logger log = Logger.get(PersistFilter.class);
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException,
			ServletException {
		
		try {
			PMF.run("prod", new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					
					Session.runInSession(req, new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							chain.doFilter(req, resp);
							
							return null;
						}
					});
					
					return null;
				}
			});
		} catch (InvocationTargetException e) {
			log.severe(e);
			
			try {
				Throwable target = e.getTargetException();
				JsonCommandServlet.writeError((HttpServletResponse)resp, target.getMessage(), target.toString());
			} catch (JSONException e1) {
				log.severe(e1);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
}
