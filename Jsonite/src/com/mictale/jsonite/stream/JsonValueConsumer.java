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

import java.util.Stack;

import com.mictale.jsonite.JsonArray;
import com.mictale.jsonite.JsonException;
import com.mictale.jsonite.JsonObject;
import com.mictale.jsonite.JsonValue;

public class JsonValueConsumer implements Consumer {

	private JsonValue name;

	private JsonValue last;
	
	private Stack<JsonValue> path = new Stack<JsonValue>();
	
	public void appendValue(JsonValue value) {
		if (!path.isEmpty()) {
			JsonValue context = path.peek();
			if (context.isArray()) {
				context.asArray().add(value);
			}
			else if (context.isObject()) {
				context.asObject().put(name.stringValue(), value);
				name = null;
			}			
		}
		
		if (!value.isPrimitive()) {
			path.push(value);
		}		
	}

	@Override
	public void append(Tuple tuple) {
		switch(tuple.getNodeType()) {
		case START_OBJECT:
			appendValue(last = new JsonObject());
			break;
		case START_ARRAY:
			appendValue(last = new JsonArray());
			break;
		case END_OBJECT:
		case END_ARRAY:
			last = path.pop();
			break;
		case MEMBER_NAME:
			name = tuple.getValue();
			break;
		case PRIMITIVE:
			last = tuple.getValue();
			appendValue(last);
			break;
		default:
			throw new JsonException("Unsupported node type: " + tuple.getNodeType());
		}
	}
	
	public JsonValue getValue() {
		if (path.size() != 0) {
			throw new IllegalStateException("The token stream is incomplete");
		}
		return last;
	}
}
