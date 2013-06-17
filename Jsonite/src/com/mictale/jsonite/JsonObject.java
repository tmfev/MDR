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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.mictale.jsonite.stream.JsonVisitor;

/**
 * A JSON Object is a set of JSON values known by name.
 * 
 * @author michael@mictale.com
 */
public final class JsonObject extends JsonValue implements Map<String, JsonValue> {

	private final Map<String, JsonValue> members;
	
	public JsonObject() {
		members = new LinkedHashMap<String, JsonValue>();
	}

	public JsonObject(Map<String, Object> members) {
		this();
		
		for (Map.Entry<String, Object> entry : members.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Always returns <code>true</code>.
	 */
	@Override
	public boolean isObject() {
		return true;
	}
	
	/**
	 * Always returns this object.
	 */
	@Override
	public JsonObject asObject() {
		return this;
	}
	
	@Override
	public JsonValue put(String key, JsonValue value) {
		if (value == null) value = JsonValue.NULL;		
		return members.put(key, value);
	}

	/**
	 * An overloaded version of {@link #put(String, JsonValue)} that allows
	 * arbitraty objects.
	 *
	 * Calling this method is identical to adding the value returned from
	 * {@link JsonValue#of(Object)}.
	 * 
	 * @param key is the member name.
	 * @param value is the new value.
	 * @return the previous value, if one existed.
	 */
	public JsonValue put(String key, Object value) {
		return put(key, JsonValue.of(value));
	}
	
	@Override
	public void clear() {
		members.clear();		
	}

	@Override
	public boolean containsKey(Object key) {
		return members.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return members.containsValue(value);
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		return members.entrySet();
	}

	@Override
	public JsonValue get(Object key) {
		JsonValue v = members.get(key);
		if (v == null) {
			return JsonValue.NULL;
		}
		else {
			return members.get(key);
		}
	}

	public JsonValue get(Object key, JsonValue defaultValue) {
		JsonValue v = members.get(key);
		return v == null ? defaultValue : v;
	}
	
	@Override
	public boolean isEmpty() {
		return members.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return members.keySet();
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonValue> m) {
		members.putAll(m);		
	}

	@Override
	public JsonValue remove(Object key) {
		return members.remove(key);
	}

	@Override
	public int size() {
		return members.size();
	}

	@Override
	public Collection<JsonValue> values() {
		return members.values();
	}

	@Override
	public JsonType getType() {
		return JsonType.OBJECT;
	}
	
	@Override
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}	
}
