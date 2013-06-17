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
package net.xclinical.odm;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	TEXT("text"),
	INTEGER("integer"),
	FLOAT("float"),
	DATE("date"),
	TIME("time"),
	DATE_TIME("datetime"),
	STRING("string"),
	BOOLEAN("boolean"),	
	DOUBLE("double"),
	HEX_BINARY("hexBinary"),
	BASE64_BINARY("base64Binary"), 
	HEX_FLOAT("hexFloat"), 
	BASE64_FLOAT("base64Float"), 
	PARTIAL_DATE("partialDate"), 
	PARTIAL_TIME("partialTime"), 
	PARTIAL_DATE_TIME("partialDatetime"), 
	DURATION_DATE_TIME("durationDatetime"), 
	INTERVAL_DATE_TIME("intervalDatetime"), 
	INCOMPLETE_DATE_TIME("incompleteDatetime"), 
	URI("URI");
	
	private final String code;
	
	private DataType(String code) {
		this.code = code;
		R.TYPES.put(code, this);
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code;
	}

	public static DataType fromCode(String code) {
		if (code == null) {
			throw new NullPointerException();
		}
		
		DataType d = R.TYPES.get(code);
		if (d == null) {
			throw new IllegalArgumentException(code + " is not a valid data type code");
		}
		return d;
	}
	
	private static final class R {
		private static final Map<String, DataType> TYPES = new HashMap<String, DataType>();
	}
}
