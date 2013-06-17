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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

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
public class BackupServlet extends HttpServlet {

	private static final long serialVersionUID = -4209050940941503330L;
	
	private static final Logger LOG = Logger.get(BackupServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Get today's date as a string:
		java.text.SimpleDateFormat todaysDate = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss");
		File backups = new File(MDRServletConfig.getBaseFolder(), "backups");
		File backupdirectory = new File(backups, todaysDate.format((java.util.Calendar.getInstance()).getTime()));

		try {
			SessionFactoryImplementor impl = (SessionFactoryImplementor) PMF.get().unwrap(Session.class).getSessionFactory();
			Connection connection = impl.getConnectionProvider().getConnection();
			
			CallableStatement cs = connection.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
			cs.setString(1, backupdirectory.getAbsolutePath());
			cs.execute();
			cs.close();
			
			resp.getWriter().println(backupdirectory.getAbsolutePath());
			resp.setStatus(HttpServletResponse.SC_OK);
			
		} catch (SQLException e) {
			LOG.severe(e);

			resp.getWriter().write(Strings.toString(e));
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
}
