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

import java.util.Random;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

public class ParsePerformanceTest {

	private static final Logger log = Logger.getAnonymousLogger();
	
	private static final int MAX_STRING_LEN = 200;

	private static final int MAX_ELEMENTS = 100;

	private static final int MAX_DEPTH = 20;

	private static final int NUM_NODES = 100;
	
	private String json;
	
	private JsonValue value;
	
	private Random r;

	private int nodes;
	
	private int depth;
	
	private char createRandomChar() {
		return (char)r.nextInt();
	}
	
	private String createRandomString() {
		int len = r.nextInt(MAX_STRING_LEN) + 1;
		char[] s = new char[len];
		for (int i = 0; i < s.length; i++) {
			s[i] = createRandomChar();
		}
		return new String(s);
	}
	
	private JsonObject createRandomObject(int maxElements) {
		depth++;
		JsonObject o = new JsonObject();
		for (int i = 0; i < maxElements && i < nodes; i++) {
			o.put(createRandomString(), createRandomValue());
		}
		depth--;
		return o;
	}
	
	private JsonArray createRandomArray() {
		depth++;
		JsonArray a = new JsonArray();
		int n = r.nextInt(MAX_ELEMENTS);
		for (int i = 0; i < n && nodes > 0; i++) {
			a.add(createRandomValue());
		}
		depth--;
		return a;		
	}
	
	private JsonValue createRandomValue() {
		int n = r.nextInt(depth > MAX_DEPTH ? 5 : 7);
		nodes--;
		switch(n) {
		case 0:
			return JsonString.of(createRandomString());
		case 1:
			return JsonNumber.of(r.nextDouble());
		case 2:
			return JsonNumber.of(r.nextLong());
		case 3:
			return JsonBoolean.of(r.nextBoolean());
		case 4:
			return JsonValue.NULL;
		case 5:
			return createRandomObject(r.nextInt(MAX_ELEMENTS));
		case 6:
			return createRandomArray();
		default:
			throw new IllegalArgumentException("Oops");
		}
	}
	
	@Before
	public void setup() {
		long start = System.currentTimeMillis();
		r = new Random(0);
		nodes = NUM_NODES;
		value = createRandomObject(Integer.MAX_VALUE);
		json = value.toString();
		int len = json.length();
		long elapsed = System.currentTimeMillis() - start;
		log.info("Constructed: " + len + " characters in " + elapsed + "ms");
	}
	
	
	@Test
	public void testPerformance() {
		long start = System.currentTimeMillis();
		log.info(this.json);
		JsonValue json = JsonValue.parse(this.json);
		long elapsed = System.currentTimeMillis() - start;
		log.info("Parsed in " + elapsed + "ms");
	}
}
