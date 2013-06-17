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
package net.xclinical.iso11179.ext;

import java.io.File;
import java.io.IOException;

import com.xclinical.mdr.server.MDRServletConfig;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.HTDigest;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.service.LoginFailedException;
import com.xclinical.mdr.service.RemoteException;

public final class Authentication {
	private static final Logger LOG = Logger.get(Authentication.class);
	
	private static final String LOGIN_FAILED_MESSAGE = "User or password unknown";
	
	private static String REALM = "trac";
	
	private static String AUTH_FILES = "/var/local/trac/users.htdigest:users.htdigest";
	
	private Authentication() {}
	
	public static HTDigest loadDigest() throws IOException {
		LOG.info("Loading digest");
		
		String authFiles = AUTH_FILES;
		for (String authFile : authFiles.split(":")) {
			LOG.info("Probing {0}", authFile);
			
			try {
				File file = new File(authFile);
				if (file.exists()) {
					LOG.info("Using {0} for authentication", file);
					
					return new HTDigest(file);					
				}

				if (!authFile.startsWith("/")) {
					file = new File(MDRServletConfig.getBaseFolder(), authFile);
					if (file.exists()) {
						LOG.info("Using {0} for authentication", file);
						return new HTDigest(file);					
					}					
				}
				
			} catch (IOException e) {
			}
		}
		
		LOG.info("Failed to find user.digest");
		throw new IOException("None of the files in " + authFiles + " could be read");
	}

	public static LoginInfo login(final String email, final String password) throws RemoteException {
		LOG.info("Attempting user login for {0}", email);

		User subject = User.findByEmail(email);
		if (subject.checkPassword(password)) {
			LOG.info("Login ok, creating session");
			Session s = Session.newSession(email);

			return new LoginInfo(subject, s.getPublicId());			
		}
		else {
			LOG.info("Login failed with invalid credentials");
			throw new LoginFailedException(LOGIN_FAILED_MESSAGE);							
		}
	}

	public static LoginInfo refresh(final String cookie) throws RemoteException {
		LOG.info("Refreshing session from cookie {0}", cookie);
		
		Session session = Session.findById(cookie);						
		return new LoginInfo(session.getSubject(), session.getPublicId());
	}

	public static void logout() throws RemoteException {
		LOG.info("Logging out");
		
		Session session = Session.get();
		PMF.get().remove(session);
	}
}
