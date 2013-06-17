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

public class JsonNull extends JsonValue {

	JsonNull() {		
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public boolean isNull() {
		return true;
	}
	
	@Override
	public String stringValue() {
		return null;
	}

	@Override
	public float floatValue() {
		return Float.NaN;
	}

	@Override
	public double doubleValue() {
		return Double.NaN;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == null || obj == this;
	}
	
	@Override
	public int hashCode() {
		return 553;
	}

	@Override
	public JsonType getType() {
		return JsonType.NULL;
	}

	@Override
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}	
}
