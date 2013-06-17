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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mictale.jsonite.stream.JsonStreamProducer;
import com.mictale.jsonite.stream.Transformation;

public class JsonStreamSourceTest {

	@Test
	public void testValue() {
		JsonStreamProducer s = new JsonStreamProducer("1.234");
		JsonValue v = Transformation.asValue(s);
		
		assertEquals(1.234, v.asNumber().doubleValue(), 1e-6);
	}
	
	@Test
	public void testEmptyObject() {
		JsonStreamProducer s = new JsonStreamProducer("{}");
		JsonValue v = Transformation.asValue(s);
		
		assertTrue(v.isObject());
	}

	@Test
	public void testEmptyArray() {
		JsonStreamProducer s = new JsonStreamProducer("[]");
		JsonValue v = Transformation.asValue(s);
		
		assertTrue(v.isArray());
	}
	
	@Test
	public void testSimpleObject() {
		JsonStreamProducer s = new JsonStreamProducer("{\"a\":1}");
		JsonValue v = Transformation.asValue(s);
		
		assertTrue(v.isObject());
		assertEquals(v.asObject().get("a"), JsonValue.of(1));
	}

	@Test
	public void testSimpleArray() {
		JsonStreamProducer s = new JsonStreamProducer("[1]");
		JsonValue v = Transformation.asValue(s);
		
		assertTrue(v.isArray());
		assertEquals(v.asArray().get(0), JsonValue.of(1));
	}

	@Test
	public void testLongerArray() {
		JsonStreamProducer s = new JsonStreamProducer("[1,2,\"a\"]");
		JsonValue v = Transformation.asValue(s);
		
		assertTrue(v.isArray());
		assertEquals(v.asArray().get(0), JsonValue.of(1));
		assertEquals(v.asArray().get(1), JsonValue.of(2));
		assertEquals(v.asArray().get(2), JsonValue.of("a"));
	}

	@Test
	public void testCascadedObjects() {
		JsonStreamProducer s = new JsonStreamProducer("{\"a\":{}}");
		JsonValue v = Transformation.asValue(s);

		assertTrue(v.isObject());
		assertTrue(v.asObject().get("a").isObject());
	}

	@Test
	public void testArrayOfObjects() {
		JsonStreamProducer s = new JsonStreamProducer("[{},{}]");
		JsonValue v = Transformation.asValue(s);

		assertTrue(v.isArray());
		assertTrue(v.asArray().get(0).isObject());
		assertTrue(v.asArray().get(1).isObject());
	}

	@Test
	public void testArrayMembers() {
		JsonStreamProducer s = new JsonStreamProducer("{\"a\":[],\"b\":[]}");
		JsonValue v = Transformation.asValue(s);

		assertTrue(v.isObject());
		assertTrue(v.asObject().get("a").isArray());
		assertTrue(v.asObject().get("b").isArray());
	}
	
}
