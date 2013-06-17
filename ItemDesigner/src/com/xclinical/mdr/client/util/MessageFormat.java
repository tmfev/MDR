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
package com.xclinical.mdr.client.util;


public class MessageFormat {

	private static final char QUOTE = '\'';
	
	public static String format(String format, Object...args) {
		boolean inQuote = false;
		int startParameter = -1;
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < format.length(); i++) {
			char ch = format.charAt(i);
			switch(ch) {
				case QUOTE:
				if (i + 1 < format.length() && format.charAt(i + 1) == QUOTE) {
					// Literal quote.
					sb.append(QUOTE);
				}
				else {
					inQuote = !inQuote;
				}
				break;
				
				case '{':
					i++;
					startParameter = i;
					break;

				case '}':
					if (startParameter > 0) {
						String parameter = format.substring(startParameter, i);
						int position = Integer.parseInt(parameter);
						sb.append(args[position]);
						startParameter = -1;
						break;
					}
					// Fall through.
				default:
					sb.append(ch);
			}
		}

		return sb.toString();
	}
}
