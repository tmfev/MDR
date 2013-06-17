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
import com.mictale.jsonite.JsonBoolean;
import com.mictale.jsonite.JsonNull;
import com.mictale.jsonite.JsonNumber;
import com.mictale.jsonite.JsonObject;
import com.mictale.jsonite.JsonString;
import com.mictale.jsonite.JsonValue;

/**
 * Iterates over a JSON tree.
 *
 * @see JsonValue#accept(JsonVisitor)
 * @author michael@mictale.com
 */
public interface JsonVisitor {

	void visit(JsonObject obj);
	
	void visit(JsonArray arr);
	
	void visit(JsonString string);

	void visit(JsonBoolean bool);

	void visit(JsonNumber number);
	
	void visit(JsonNull nul);
}
