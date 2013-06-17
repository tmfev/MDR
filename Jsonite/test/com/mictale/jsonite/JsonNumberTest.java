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

public class JsonNumberTest {

	@Test
	public void testInteger() {
		JsonNumber n = JsonNumber.of(1).asNumber();
		assertEquals(1, n.intValue());
	}

	@Test
	public void testNegativeInteger() {
		JsonNumber n = JsonNumber.of(-345543).asNumber();
		assertEquals(-345543, n.intValue());
	}
	
	@Test
	public void testDouble() {
		JsonNumber n = JsonNumber.parse("3.1415").asNumber();
		assertEquals(3.1415, n.doubleValue(), 0.01);
	}
	
	@Test
	public void testOf() {
		assertEquals((byte)1, JsonNumber.of((byte)1).byteValue());
		assertEquals((short)1, JsonNumber.of((short)1).shortValue());
		assertEquals(1, JsonNumber.of(1).intValue());
	}
	
}
