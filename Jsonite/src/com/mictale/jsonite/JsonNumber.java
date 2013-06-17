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
 * A numeric value.
 *
 * You create a new instance using {@link #of(Number)} or
 * {@link #of(Object)}. The first method should be preferred
 * if you know the value you are passing into the method is a number.
 * 
 * @author michael@mictale.com
 */
public final class JsonNumber extends JsonValue {

	private final Number value;
	
	private JsonNumber(Number value) {
		if (value == null) throw new IllegalArgumentException();
		this.value = value;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public JsonNumber asNumber() {
		return this;
	}
	
	@Override
	public byte byteValue() {
		return value.byteValue();
	}
	
	@Override
	public short shortValue() {
		return value.shortValue();
	}
	
	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}
	
	@Override
	public float floatValue() {
		return value.floatValue();
	}
	
	@Override
	public double doubleValue() {
		return value.doubleValue();
	}
	
	@Override
	public String stringValue() {
		return value.toString();
	}
	
	public Number numberValue() {
		return value;
	}
	
	/**
	 * Converts the specified number to a {@link JsonValue}.
	 * 
	 * This overload of {@link JsonValue#of(Object)} is optimized for numeric input.
	 * The returned type will either be a {@link JsonNumber} if the input is a number
	 * or {@link JsonValue#NULL} if the input is <code>null</code>.
	 * 
	 * @param n is the number to convert.
	 * @return the JSON number.
	 */
	public static JsonValue of(Number n) {
		if (n == null) {
			return JsonValue.NULL;
		}
		else {		
			if (n.longValue() == n.doubleValue()) {
				return new JsonNumber(n.longValue());			
			}
			else {
				return new JsonNumber(n.doubleValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof JsonNumber && ((JsonNumber)obj).value.equals(value);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public JsonType getType() {
		return JsonType.NUMBER;
	}
	
	@Override
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}
}
