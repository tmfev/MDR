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
package com.xclinical.mdr.util;

import java.util.ArrayList;

import junit.framework.TestCase;

public class HashTreeTest extends TestCase {

	public void testPut() {
		HashTree<String> ht = new HashTree<String>();
		
		ht.put("1", "2", "3", "4");
		
		assertEquals(1, ht.get("1").size());
		assertEquals(1, ht.get("1", "2").size());
		assertTrue(ht.get("1").contains("2"));
		assertTrue(ht.get("1", "2").contains("3"));
		assertTrue(ht.get("1", "2", "3").contains("4"));
	}

	public void testKeys() {
		HashTree<String> ht = new HashTree<String>();
		
		ht.put("1", "21", "31", "41");
		ht.put("1", "22", "32", "42");

		ArrayList<String> keys = new ArrayList<String>(ht.get("1"));
		assertTrue(keys.remove("21"));
		assertTrue(keys.remove("22"));
		assertTrue(keys.isEmpty());
	}
}
