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

import org.junit.Test;
import static org.junit.Assert.*;

public class JsonArrayTest {

	@Test
	public void testAdd() {
		JsonArray a = new JsonArray();
		a.add(JsonValue.of(true));
		a.add(JsonValue.of(1));
		a.add(JsonValue.of("foo"));
		
		assertEquals(3, a.size());
		assertTrue(a.get(0).asBoolean().booleanValue());
		assertEquals(1, a.get(1).asNumber().intValue());
	}

}
