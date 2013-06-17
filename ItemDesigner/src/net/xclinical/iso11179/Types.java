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
package net.xclinical.iso11179;

import java.util.HashMap;
import java.util.Map;

public final class Types {

	private static final Map<String, Class<?>> TYPE_CACHE = new HashMap<String, Class<?>>();

	private static final Map<Class<?>, String> NAME_CACHE = new HashMap<Class<?>, String>();

	private Types() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T getType(String urn) throws TypeNotFoundException {
		Class<?> type = TYPE_CACHE.get(urn);
		if (type == null) {
			String simpleName = urn.substring(urn.lastIndexOf(":") + 1);
			try {
				type = Class.forName(Types.class.getPackage().getName() + "." + simpleName);
			} catch (ClassNotFoundException e) {
				try {
					type = Class.forName(Types.class.getPackage().getName() + ".ext." + simpleName);
				} catch (ClassNotFoundException e2) {
					throw new TypeNotFoundException(urn);
				}
			}
			TYPE_CACHE.put(urn, type);
			NAME_CACHE.put(type, urn);
		}

		return (T) type;
	}

	public static <T> T newInstance(String urn) {
		try {
			Class<?> clazz = Types.getType(urn);
			T instance = (T) clazz.newInstance();
			return instance;
		} catch (TypeNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String getUrn(Class<?> type) throws TypeNotFoundException {
		String urn = NAME_CACHE.get(type);
		if (urn == null) {
			throw new TypeNotFoundException(type.getName());
		}
		return urn;
	}
}
