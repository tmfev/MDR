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
package com.mictale.jsonite;

/**
 * Contains all symbols that have a special meaning in JSON.
 * 
 * @author michael@mictale.com
 */
public interface JsonSyntax {

	public static final char ARRAY_BEGIN = '[';
	
	public static final char ARRAY_END = ']';

	public static final char OBJECT_MEMBER_SEPARATOR = ':';
	
	public static final char SEPARATOR = ',';
	
	public static final char OBJECT_END = '}';
	
	public static final char OBJECT_BEGIN = '{';

	public static final String NULL = "null";

	public static final String BOOLEAN_FALSE = "false";

	public static final String BOOLEAN_TRUE = "true";

	public static final char STRING_QUOTE = '\"';

	public static final char STRING_ESCAPE = '\\';

	public static final char ESCAPE_SOLIDUS = '/';
	
	public static final char ESCAPE_NEWLINE = 'n';

	public static final char ESCAPE_FEED = 'f';

	public static final char ESCAPE_TAB = 't';

	public static final char ESCAPE_RETURN = 'r';
	
	public static final char ESCAPE_BACKSPACE = 'b';

	public static final char NUMBER_ENGINEERING_UPPER = 'E';

	public static final char NUMBER_ENGINEERING = 'e';

	public static final char NUMBER_SIGN = '-';

	public static final char NUMBER_DECIMAL = '.';
}
