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
package net.xclinical.mdt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.xclinical.mdr.server.util.Logger;

public class Log {

	private static final Logger log = Logger.get(Log.class);
	
	private final ByteArrayOutputStream stm;

	private final PrintStream w;
	
	private static final ThreadLocal<Log> currentLogger = new ThreadLocal<Log>();
	
	private Log() {
		stm = new ByteArrayOutputStream(10 * 1024 * 1024);
		w = new PrintStream(stm);
	}
	
	private static Log log() {
		Log l = currentLogger.get();
		if (l == null) {
			l = new Log();
			currentLogger.set(l);
		}
		return l;
	}
	
	private void msg(String format, Object...args) {
		msg(String.format(format, args));
	}

	private void msg(String str) {
		log.info(str);
		w.println(str);
	}
	
	public static final void m(String format, Object...args) {
		log().msg(format, args);
	}

	public static final void m(String str) {
		log().msg(str);
	}

	public static final void e(Throwable e) {
		StringWriter sw = new StringWriter();
				
		sw.append("A ");
		sw.append(e.getClass().getName());
		sw.append(" exception\n");
		sw.append(e.getMessage());
		sw.append("\n");		
		e.printStackTrace(new PrintWriter(sw));
		
		log().msg(sw.toString());
	}
	
	public static byte[] toByteArray() {
		Log l = log();
		l.w.flush();
		byte[] arr = l.stm.toByteArray();
		currentLogger.remove();
		return arr;
	}
	
	public static PrintStream stm() {
		return log().w;
	}
}
