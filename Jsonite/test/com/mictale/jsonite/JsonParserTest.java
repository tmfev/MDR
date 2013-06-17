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

import java.io.StringReader;

import org.junit.Test;

import com.mictale.jsonite.stream.JsonStreamProducer;
import com.mictale.jsonite.stream.JsonValueConsumer;
import com.mictale.jsonite.stream.Transformation;

public class JsonParserTest {

	@Test
	public void testEmptyObject() {
		JsonValue json = JsonValue.parse("{}");
		JsonObject obj = json.asObject();
		
		assertEquals(obj.isEmpty(), true);
	}

	@Test
	public void testSimpleObject() {
		JsonValue json = JsonValue.parse("{\"foo\":\"bar\"}");
		JsonObject obj = json.asObject();
		
		assertEquals(obj.isEmpty(), false);
		assertEquals("bar", obj.get("foo").stringValue());
	}

	@Test
	public void testAdvancedObject() {
		JsonValue json = JsonValue.parse("{\"foo\":\"bar\",\"foo2\":1234.5}");
		JsonObject obj = json.asObject();
		
		assertEquals(obj.isEmpty(), false);
		assertEquals(obj.containsKey("foo"), true);
		assertEquals(obj.containsKey("foo2"), true);
		assertEquals(obj.containsKey("foo3"), false);
	}
	
	@Test
	public void testNumber() {
		assertEquals(3.1415, JsonValue.parse("3.1415").doubleValue(), 1e-6);		
		assertEquals(1, JsonValue.parse("1").doubleValue(), 1e-6);		
		assertEquals(-1, JsonValue.parse("-1").doubleValue(), 1e-6);		
		assertEquals(1e10, JsonValue.parse("1e10").doubleValue(), 1e-6);		
		assertEquals(1e-3, JsonValue.parse("1e-3").doubleValue(), 1e-6);		
	}
	
	@Test
	public void testEmptyArray() {
		JsonValue json = JsonValue.parse("[]");
		JsonArray arr = json.asArray();
		
		assertEquals(arr.isEmpty(), true);
	}

	@Test
	public void testValueArray() {
		JsonValue json = JsonValue.parse("[1,2,3]");
		JsonArray arr = json.asArray();
		
		assertEquals(arr.isEmpty(), false);
		assertEquals(arr.size(), 3);
		assertEquals(1, arr.get(0).asNumber().intValue());
		assertEquals(2, arr.get(1).asNumber().intValue());
		assertEquals(3, arr.get(2).asNumber().intValue());
	}

	@Test
	public void testObjectArray() {
		JsonValue json = JsonValue.parse("[{},{\"a\":4}]");
		JsonArray arr = json.asArray();
		
		assertEquals(arr.isEmpty(), false);
		assertEquals(arr.size(), 2);
		assertEquals(4, arr.get(1).asObject().get("a").asNumber().intValue());
	}

	@Test
	public void testTrue() {
		JsonValue json = JsonValue.parse("true");		
		assertEquals(true, json.asBoolean().booleanValue());
	}

	@Test
	public void testFalse() {
		JsonValue json = JsonValue.parse("false");		
		assertEquals(false, json.asBoolean().booleanValue());
	}

	@Test
	public void testArrayInObject() {
		final String s =
		"{\n" +
		"	    \"children\": [\n" +
		"	        1\n" +
		"	    ]\n" +
		"	}\n";

		JsonValueConsumer c = new JsonValueConsumer();
		Transformation.copy(c, new JsonStreamProducer(new StringReader(s)));
		JsonObject o = c.getValue().asObject();
		assertEquals(1, o.get("children").asArray().get(0).asNumber().intValue());
		
	}
	
	@Test
	public void testCfg() {
		final String s = "{\"map\":{\"center\":[0,0],\"pixelPerDegree\":1000}}";
		JsonObject.parse(s);
	}
}
