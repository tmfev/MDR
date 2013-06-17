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

import com.mictale.jsonite.stream.JsonVisitor;

/**
 * Represents a JSON string.
 *
 * A JSON string is an immutable primitive. 
 * 
 * @author michael@mictale.com
 */
public class JsonString extends JsonValue {

	private final String value;
	
	private JsonString(String value) {
		if (value == null) throw new NullPointerException();
		this.value = value;
	}

	/**
	 * Creates a new JSON string from the specified string value.
	 * 
	 * @param value is the string value.
	 * @return the JSON value.
	 * @throws NullPointerException if the specified string is <code>null</code>.
	 */
	public static JsonValue of(String value) {
		if (value == null) {
			return JsonValue.NULL;
		}
		else {
			return new JsonString(value);
		}
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public boolean isString() {
		return true;
	}

	@Override
	public JsonString asString() {
		return this;
	}
	
	@Override
	public String stringValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof JsonString && ((JsonString)obj).value.equals(value);
	}

	@Override
	public JsonType getType() {
		return JsonType.STRING;
	}
	
	@Override
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}	
}
