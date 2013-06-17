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
package com.xclinical.mdr.repository;

import java.io.Serializable;

public class Key implements Serializable, Comparable<Key>, HasKey {

	private static final long serialVersionUID = 4385015956345890923L;

	private String name;
	
	private Long value;
	
	public static final Key INVALID = create("urn:type:invalid", 0);

	private static final char SEPARATOR = '@';

	public Key() {}

	public Key(String key) {
		if (key == null) throw new NullPointerException();
		
		int idx = key.lastIndexOf(SEPARATOR);
		if (idx == -1) {
			name = key;
			value = null;
		}
		else {
			name = key.substring(0, idx);
			value = Long.parseLong(key.substring(idx + 1)); 
		}
	}
	
	private Key(String name, Long value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public Key getKey() {
		return this;
	}
	
	public boolean isValid() {
		return !equals(INVALID);
	}
	
	public String getName() {
		return name;
	}

	public String getSimpleName() {
		int ind = name.lastIndexOf(':');
		return name.substring(ind + 1);
	}
	
	public Long getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}
	
	@Override
	public int compareTo(Key o) {
		return value.compareTo(o.getValue());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Key && ((Key)obj).name.equals(name) && ((Key)obj).value.equals(value); 
	}

	@Override
	public int hashCode() {
		return name.hashCode() + (value == null ? 0xacf379 : value.hashCode());
	}
	
	@Override
	public String toString() {
		if (value == null) {
			return name;
		}
		else {
			return name + SEPARATOR + value;
		}
	}

	public static Key create(String name, long value) {
		return new Key(name, value);
	}
	
	public static Key parse(String key) {
		return new Key(key);
	}
}
