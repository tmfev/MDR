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

public final class JsonBoolean extends JsonValue {

	private final boolean value;
	
	JsonBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public JsonBoolean asBoolean() {
		return this;
	}
	
	public boolean booleanValue() {
		return value;
	}

	/**
	 * Returns the JSON instance of the specified {@link Boolean}.
	 * 
	 * The method will always return either {@link JsonValue#NULL},
	 * {@link JsonValue#TRUE} or {@link JsonValue#FALSE}.
	 * 
	 * @param b is the boolean value to convert.
	 * @return the JSON value representing the specified boolean.
	 */
	public static JsonValue of(Boolean b) {
		if (b == null) {
			return JsonValue.NULL;
		}
		else {
			if (b.booleanValue()) {
				return JsonValue.TRUE;
			} else {
				return JsonValue.FALSE;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this || ((obj instanceof Boolean) && ((Boolean)obj).equals(value));
	}
	
	@Override
	public int hashCode() {
		return value ? 1231 : 1237;
	}

	@Override
	public JsonType getType() {
		return JsonType.BOOLEAN;
	}
	
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}
}
