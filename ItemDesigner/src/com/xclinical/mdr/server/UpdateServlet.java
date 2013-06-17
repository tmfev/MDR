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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import com.google.inject.Singleton;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.shared.Strings;

@Singleton
public class UpdateServlet extends HttpServlet {

	private static final long serialVersionUID = -4209050940941503330L;
	
	private static final Logger LOG = Logger.get(UpdateServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int count = 0;
		String line = null;
		
		try {
			SessionFactoryImplementor impl = (SessionFactoryImplementor) PMF.get().unwrap(Session.class).getSessionFactory();
			Connection connection = impl.getConnectionProvider().getConnection();
			
			final String sql = req.getPathInfo().substring(1);
			InputStream stm = getClass().getClassLoader().getResourceAsStream("META-INF/" + sql);
			
			if (stm == null) {
				throw new Exception("The resource " + sql + " was not found"); 
			}
			
			BufferedReader r = new BufferedReader(new InputStreamReader(stm));
			
			while ((line = r.readLine()) != null) {
				if (line.length() > 0 && !line.startsWith("//")) {
					PreparedStatement stmt = connection.prepareStatement(line);
					stmt.execute();
					stmt.close();
				}
				count++;
			}
						
			resp.getWriter().println("Executed " + count + " lines, check your log files");
			resp.setStatus(HttpServletResponse.SC_OK);
			
		} catch (Exception e) {
			LOG.severe(e);

			resp.getWriter().println("Scipt failed in line " + count + ": " + line);
			resp.getWriter().write(Strings.toString(e));
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
}
