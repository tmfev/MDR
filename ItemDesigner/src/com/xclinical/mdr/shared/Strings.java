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
package com.xclinical.mdr.shared;

public class Strings {

	public static String toString(Throwable e) {
		StringBuilder sb = new StringBuilder(e.getClass().toString() + ":\n");
		sb.append("  " + e.getLocalizedMessage() + "\n\n");
		
		while (e != null) {
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			sb.append(e.toString() + "\n");
			for (int i = 0; i < stackTraceElements.length; i++) {
				sb.append("    at " + stackTraceElements[i] + "\n");
			}
			e = e.getCause();
			if (e != null) {
				sb.append("Caused by: ");
			}
		}
		
		return sb.toString();
	}

}