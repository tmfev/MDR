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
package com.xclinical.mdr.server.util;

import java.io.Writer;

/**
 * Writes log messages for the derby database.
 * 
 * @author michael@mictale.com
 */
public class DerbyLogWriter extends Writer {
	private static final Logger log = Logger.get(DerbyLogWriter.class);

	public DerbyLogWriter() {
		super();
	}

	public static Writer getLogWriter() {
		return new DerbyLogWriter();
	}

	/**
	 * Registers this writer so that derby uses it.
	 * 
	 */
	public static void register() {
		System.setProperty("derby.stream.error.method", DerbyLogWriter.class.getName() + ".getLogWriter");
	}

	public void close() {
	}

	public void flush() {
	}

	public void write(char[] cbuf) {
		write(cbuf, 0, cbuf.length);
	}

	public void write(char[] cbuf, int off, int len) {
		write(new String(cbuf, off, len));
	}

	public void write(int c) {
		write(new char[] { (char) c });
	}

	public void write(String str) {
		write(str, 0, str.length());
	}

	public void write(String str, int off, int len) {
		String msg = str.substring(off, off + len);
		String[] parts = msg.split("\n");
		for (int i = 0; i < parts.length; i++) {
			log.debug(parts[i].trim());
		}
	}
}
