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
import java.util.concurrent.Callable;

import net.xclinical.mdt.FileSource;
import net.xclinical.mdt.Log;
import net.xclinical.mdt.Source;

import com.xclinical.mdr.server.impexp.Imports;
import com.xclinical.mdr.server.util.Logger;

/**
 * A command line tool to import data.
 * 
 * @author ms@xclinical.com
 */
public class Import {

	private static final Logger log = Logger.get(Import.class);
	
	public static final void main(final String[] argv) throws Exception {
		PMF.run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				log.info("Starting import");

				File currentDir = new File(".");
				
				log.info("Working in " + currentDir.getAbsolutePath());
				
				String formatFileName = argv[0];
				log.info("Format file is " + formatFileName);
				
				File formatFile = new File(formatFileName);
				Source format = new FileSource(formatFile);

				String dataFileName = argv[1];
				log.info("Data file is " + dataFileName);
				
				File dataFile = new File(dataFileName);
				Source data = new FileSource(dataFile);		
						
				Imports.doImports(format, data);
				
				String l = new String(Log.toByteArray());
				log.debug(l);
				
				return null;
			}
		});
	}
}
