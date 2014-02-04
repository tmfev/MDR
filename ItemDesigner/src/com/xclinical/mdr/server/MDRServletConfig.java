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
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.servlet.ServletContextEvent;

import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.ext.Authentication;
import net.xclinical.iso11179.ext.User;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.xclinical.mdr.server.util.DerbyLogWriter;
import com.xclinical.mdr.server.util.HTDigest;
import com.xclinical.mdr.server.util.Logger;

public class MDRServletConfig extends GuiceServletContextListener {

	private static final Logger LOG = Logger.get(MDRServletConfig.class);
	
	private static File baseFolder;
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		LOG.info("Initializing MDR servlet configuration");
		
		DerbyLogWriter.register();
		
		super.contextInitialized(servletContextEvent);		
		
		LOG.info("Guice initialized");

		try {
			PMF.run("prod", new Callable<Void>() {
				@Override
				public Void call() throws Exception {
	
					net.xclinical.iso11179.Context.initializeRoot();

					String type = PMF.getColumnType("DESIGNATION", "SIGN");					
					if (!"VARCHAR(4000)".equals(type)) {
						PMF.executeSql("alter table DESIGNATION alter column SIGN set data type varchar(4000)");
					}

					type = PMF.getColumnType("DEFINITION", "TEXT");					
					if (!"VARCHAR(4000)".equals(type)) {
						PMF.executeSql("alter table DEFINITION alter column TEXT set data type varchar(4000)");
					}

					type = PMF.getColumnType("INDEX_ELEMENT", "TOKEN");					
					if (!"VARCHAR(4000)".equals(type)) {
						PMF.executeSql("alter table INDEX_ELEMENT alter column TOKEN set data type varchar(4000)");
					}
					
					// Convert existing users.
					HTDigest ht = Authentication.loadDigest();
					
					net.xclinical.iso11179.Context root = net.xclinical.iso11179.Context.root();
					LanguageIdentification lang = LanguageIdentification.findOrCreate("en-US");
					
					for (Map.Entry<HTDigest.Key, String> entry : ht.entrySet()) {
						HTDigest.Key key = entry.getKey();
						User subject;
						final String email = key.getUser();
						try {
							subject = User.findByEmail(email);
							if (!subject.hasPassword()) {
								subject.setHtAccess(key.getUser(), key.getRealm(), entry.getValue());
								PMF.get().persist(subject);								
							}
						}
						catch(NoResultException e) {
							subject = new User();
							subject.setEmail(email);
							subject.setHtAccess(key.getUser(), key.getRealm(), entry.getValue());
							PMF.get().persist(subject);
						}						
						
						root.designate(subject, key.getUser(), lang);
					}
					
					return null;
				}
			});
		} catch (InvocationTargetException e) {
			LOG.severe(e);			
		}		
	}
	
	@Override
	protected Injector getInjector() {
		LOG.info("Retrieving injector");
		return Guice.createInjector(new MDRModule());
	}

	public static File getBaseFolder() {
		if (baseFolder == null) {
			LOG.info("Determining base folder");
	    	final String baseDir;
	    	
	    	String mdr = null;
	    	
	    	try {
	    		LOG.info("Trying JNDI");
	    		
		    	Context context = (Context) new InitialContext().lookup("java:comp/env");
		    	mdr = (String) context.lookup("MDR");
	    	}
	    	catch(NamingException e) {
	    		LOG.severe(e);
	    	}
	    	
	    	if (mdr == null) {
	    		LOG.info("No JNDI, trying system environment");
	    		
	    		mdr = System.getenv("MDR");
	    	}
	    	
	    	if (mdr != null && mdr.length() > 0) {
				LOG.info("Taking base directory from process environment: " + mdr);		    	
	    		baseDir = mdr;
	    	}
	    	else {        	
	    		LOG.info("Falling back to user's home directory");
	    		final String home = System.getenv("HOME");
	    		baseDir = home + "/mdr";
	    	}

	    	LOG.info("Using base directory: " + baseDir);
	    	
	    	baseFolder = new File(baseDir);
	    	if (!baseFolder.exists()) {
	    		throw new IllegalArgumentException("Could not find base folder: " + baseFolder + ", please set MDR in your environment or make ~/mdr accessible");
	    	}
	    	if (!baseFolder.canWrite()) {
	    		throw new IllegalArgumentException("Cannot write to base folder " + baseFolder);
	    	}		
		}
		
		return baseFolder;
	}
}
