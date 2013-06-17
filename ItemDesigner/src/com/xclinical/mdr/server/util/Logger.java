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

import java.text.MessageFormat;

public class Logger {

	private final org.apache.log4j.Logger log;
	
	private Logger(Class<?> clazz) {
		log = org.apache.log4j.Logger.getLogger(clazz);
	}

	public static Logger get(Class<?> clazz) {
		return new Logger(clazz);
	}

	public void trace(String msg) {
		log.trace(msg);
	}

	public void trace(String pattern, Object... args) {
		trace(MessageFormat.format(pattern, args));
	}	
	
	public void debug(String msg) {
		log.debug(msg);
	}

	public void debug(String pattern, Object... args) {
		debug(MessageFormat.format(pattern, args));
	}	
	
	public void info(String msg) {
		log.info(msg);
	}

	public void info(String pattern, Object... args) {
		info(MessageFormat.format(pattern, args));
	}
	
	public void severe(String msg) {
		log.error(msg);
	}
	
	public void severe(String pattern, Object... args) {
		severe(MessageFormat.format(pattern, args));
	}
	
	public void severe(Throwable e) {
		severe(Exceptions.toString(e));
	}
	
}
