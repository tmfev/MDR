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

public class JsonValueTest {

	@Test
	public void testOf() {
		assertEquals((byte)1, JsonValue.of((byte)1).byteValue());
		assertEquals((short)1, JsonValue.of((short)1).shortValue());
		assertEquals((int)1, JsonValue.of((int)1).intValue());
		assertEquals((long)1, JsonValue.of((long)1).longValue());
		assertEquals(1.234f, JsonValue.of(1.234f).floatValue(), 1e-6);
		assertEquals(1.234, JsonValue.of(1.234).doubleValue(), 1e-6);
	}

	@Test
	public void testTrue() {
		assertTrue(JsonValue.of(true).asBoolean().booleanValue());
		assertTrue(JsonValue.of(Boolean.TRUE).asBoolean().booleanValue());
	}

	@Test
	public void testFalse() {
		assertFalse(JsonValue.of(false).asBoolean().booleanValue());
		assertFalse(JsonValue.of(Boolean.FALSE).asBoolean().booleanValue());
	}
	
	@Test
	public void testParse() {
		final String json = 
		"{\n" +
		"   \"id\": \"fhlckwexuepxr\",\n" +
		"   \"user\": {\n" +
		"       \"id\": \"ofqaudim\",\n" +
		"       \"permissions\": [\n" +
		"           {\n" +
		"               \"types\": 3,\n" +
		"               \"owner\": \"ofqaudim\",\n" +
		"               \"label\": \"Michael (michael@mictale.com)\"\n" +
		"           }\n" +
		"       ],\n" +
		"       \"macros\": [\n" +
		"           {\n" +
		"               \"label\": \"Confirm Email\",\n" +
		"               \"id\": \"agltYXBmaW5pdHlyIQsSCFJlc291cmNlIghvZnFhdWRpbQwLEgVNYWNybxgRDA\"\n" +
		"           }\n" +
		"       ],\n" +
		"       \"author\": \".admin\",\n" +
		"       \"properties\": {\n" +
		"           \"author\": \".admin\",\n" +
		"           \"com.mapfinity.property.label\": \"Michael\",\n" +
		"           \"com.mapfinity.property.email\": \"michael@mictale.com\",\n" +
		"           \"com.mapfinity.property.size\": 0\n" +
		"       }\n" +
		"   },\n" +
		"   \"avatar\": \"http:\\/\\/www.gravatar.com\\/avatar\\/d09d3933216ac1d34bc66d9ca26fc9e3\",\n" +
		"   \"expires\": 1333573345548\n" +
		"}";
		
		JsonValue j = JsonValue.parse(json);
		assertTrue(j.isObject());
	}
}
