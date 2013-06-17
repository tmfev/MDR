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
package com.mictale.jsonite.stream;

import com.mictale.jsonite.JsonValue;

/**
 * A single element provided by a {@link Producer} and consumed by a
 * {@link Consumer}. A tuple is a combination of a {@link JsonNodeType}
 * and a {@link JsonValue}.
 * 
 * @author michael@mictale.com
 */
public class Tuple {
	private final JsonNodeType nodeType;
	private final JsonValue value;

	public Tuple(JsonNodeType nodeType, JsonValue value) {
		this.nodeType = nodeType;
		this.value = value;
	}

	/**
	 * Returns the node type of this tuple.
	 * 
	 * @return the node type.
	 */
	public JsonNodeType getNodeType() {
		return nodeType;
	}

	/**
	 * Returns the value of this tuple or <code>null</code> if
	 * the value is not defined.
	 * 
	 * @return the value of this tuple.
	 */
	public JsonValue getValue() {
		return value;
	};
	
	@Override
	public String toString() {
		return nodeType.toString() + ":" + String.valueOf(value);
	}
}
