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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.fileupload.util.Streams;

import com.xclinical.mdr.server.util.Logger;

public class ZipResourceResolver implements ResourceResolver {

	private static final Logger LOG = Logger.get(ZipResourceResolver.class);
	
	private final ZipFile file;
	
	public ZipResourceResolver(InputStream stm) throws IOException {
		File file = File.createTempFile("mdrres", ".tmp");
		FileOutputStream out = new FileOutputStream(file);
		Streams.copy(stm, out, true);
		this.file = new ZipFile(file);
	}
	
	@Override
	public InputStream open(String name) throws IOException {
		ZipEntry entry = file.getEntry(name);
		if (entry == null) {
			Enumeration<? extends ZipEntry> e = file.entries();
			while (e.hasMoreElements()) {
				LOG.severe(e.nextElement().getName());
			}
			throw new IOException(name + " not found");
		}
		return file.getInputStream(entry);
	}
}
