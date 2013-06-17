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
package com.xclinical.mdr.server.io.odm;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	TEXT("text"),
	INTEGER("integer"),
	FLOAT("float"),
	DATE("date"),
	TIME("time"),
	DATETIME("datetime"),
	STRING("string"),
	BOOLEAN("boolean"),
	DOUBLE("double"),
	HEX_BINARY("hexBinary"),
	BASE64_BINARY("base64Binary"),
	HEX_FLOAT("hexFloat"),
	BASE64_FLOAT("base64Float"),
	PARTIAL_DATE("partialDate"),
	PARTIAL_TIME("partialTime"),
	PARTIAL_DATETIME("partialDatetime"),
	DURATION_DATETIME("durationDatetime"),
	INTERVAL_DATETIME("intervalDatetime"),
	INCOMPLETE_DATETIME("incompleteDatetime"),
	URI("URI");
	
	private final String name;
	
	private DataType(String name) {
		this.name = name;
		R.add(this);
	}
	
	public String getName() {
		return name;
	}
	
	public static DataType fromName(String name) {
		DataType d = R.get(name);
		if (d == null) throw new IllegalArgumentException("The data type name " + name + " was not found");
		return d;
	}
	
	private static final class R {
		private static final Map<String, DataType> NAMES = new HashMap<String, DataType>();
		
		private static void add(DataType t) {
			NAMES.put(t.getName(), t);
		}
		
		private static DataType get(String name) {
			DataType t = NAMES.get(name);
			if (t == null) throw new IllegalArgumentException(name);
			return t;
		}
	}
}
