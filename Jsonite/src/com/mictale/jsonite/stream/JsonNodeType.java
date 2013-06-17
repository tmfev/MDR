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

import com.mictale.jsonite.JsonArray;
import com.mictale.jsonite.JsonObject;
import com.mictale.jsonite.JsonString;


/**
 * Specifies the type of element in a {@link Tuple}.
 * 
 * @author michael@mictale.com
 */
public enum JsonNodeType {

	/**
	 * The beginning of a {@link JsonObject}.
	 * 
	 * The value returned by {@link Tuple#getValue()} can be either the {@link JsonObject}
	 * that just begun or <code>null</code> if the stream implementation does not support
	 * composite objects. 
	 */
	START_OBJECT,

	/**
	 * A member of a {@link JsonObject}.
	 * 
	 * The value returned by {@link Tuple#getValue()} is a {@link JsonString} that represents
	 * the name of the member.
	 */
	MEMBER_NAME,

	/**
	 * The end of a {@link JsonObject}.
	 * 
	 * The value returned by {@link Tuple#getValue()} can be either the {@link JsonObject}
	 * that just ended or <code>null</code> if the stream implementation does not track
	 * composite objects. 
	 */
	END_OBJECT,

	/**
	 * The start of a {@link JsonArray}.
	 * 
	 * The value returned by {@link Tuple#getValue()} can be either the {@link JsonArray}
	 * that just begun or <code>null</code> if the stream implementation does not track
	 * composite objects. 
	 */
	START_ARRAY,

	/**
	 * The end of a {@link JsonArray}.
	 * 
	 * The value returned by {@link Tuple#getValue()} can be either the {@link JsonArray}
	 * that just ended or <code>null</code> if the stream implementation does not track
	 * composite objects. 
	 */
	END_ARRAY,

	/**
	 * A primitive value.
	 * 
	 * The value returned by {@link Tuple#getValue()} is either a top level element or a 
	 * member of a {@link JsonObject} or {@link JsonArray}. A {@link JsonObject} or 
	 * {@link JsonArray} itself will never produce this node type.
	 */
	PRIMITIVE, 
	
	/**
	 * The stream is at an unspecified position.
	 */
	UNDEFINED
}
