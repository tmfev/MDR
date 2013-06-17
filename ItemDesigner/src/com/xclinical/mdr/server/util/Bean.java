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
package com.xclinical.mdr.server.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides meta information about types that implement bean semantics.
 * 
 * Call {@link Beans#fromType(Class)} to retrieve an instance of this class.
 * 
 * A bean is a java class that has methods to access its properties. These methods
 * have to comply with a specific syntax: To retrieve the property value, a method
 * <code>T get<em>Name</em>()</code> must exist that returns the property value of
 * type <code>T</code>. To change a value, a method <code>set<em>Name</em>(T t)</code>
 * must exist. If one of the methods is missing, the value is either read- or write-only.
 *
 * This class supports the {@link Named} and <code>DefaultXXXValue</code> annotations.
 * @author michael@mictale.com
 */
public final class Bean {

	private final Class<?> type;
	
	private String name;
	
	private final Map<String, Property> properties = new HashMap<String, Property>();
	
	Bean(Class<?> type) {
		this.type = type;

		Named named = type.getAnnotation(Named.class);
		if (named == null) {
			name = type.getSimpleName();
		}
		else {
			name = named.value();
		}
		
		parseType(type);
	}

	/**
	 * Retrieves the name of the bean.
	 * 
	 * The name of a bean is either the simple class name or the value of
	 * the {@link Named} annotation of class.
	 * 
	 * @return the name of the bean.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves the type that is implementing this bean.
	 * 
	 * @return the implementation type.
	 */
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * Retrieves meta information about a property.
	 * 
	 * @param name is the name of the property.
	 * @return the property or <code>null</code> if there is no such property.
	 */
	public Property getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Retrieves all properties.
	 * 
	 * @return all properties.
	 */
	public Collection<Property> getProperties() {
		return properties.values();
	}

	/**
	 * Sets a property value of a specific bean.
	 * 
	 * @param bean is the bean whose property should be set.
	 * @param name is the name of the property.
	 * @param value is the value of the property.
	 */
	public void setValue(Object bean, String name, Object value) {
		getProperty(name).setValue(bean, value);
	}
	
	/**
	 * Retrieves the value of a specific bean.
	 * 
	 * @param bean is the bean whose property should be set.
	 * @param name is the name of the property.
	 * @return the property value.
	 */
	public Object getValue(Object bean, String name) {
		Property property = getProperty(name);
		if (property == null) {
			throw new IllegalArgumentException("No such property: " + name);
		}
			
		return property.getValue(bean);
	}

	private Property createProperty(String name) {
		Property p = properties.get(name);
		if (p == null) {
			p = new Property(name);
			properties.put(name, p);
		}		
		
		return p;
	}
	
	/**
	 * Constructs a property name from a method name a prefix.
	 * if the prefix does not match, returns <code>null</code>.
	 * 
	 * @param method is the method name.
	 * @param prefix is the prefix to check.
	 * @return the property name or <code>null</code>.
	 */
	private static String propertyName(Named named, String method, String prefix) {
		if (named == null || named.value().length() == 0) { 
			if (method.startsWith(prefix) && method.length() > prefix.length()) {
				char[] c = method.toCharArray();
				// Lowercase first character.
				c[prefix.length()] = Character.toLowerCase(c[prefix.length()]);
				// Drop prefix.
				return String.copyValueOf(c, prefix.length(), c.length - prefix.length());
			}
			else {
				return null;
			}
		}
		else {
			return named.value();
		}
	}

	/**
	 * Parses the specified type and adds all properties to the specified map.
	 * The property map is used for bookkeeping of the total list of properties. It is
	 * discarded when parsing is complete.
	 * <p>
	 * The method parses the specified interface first, then all its super interfaces.
	 * 
	 * @param properties keeps a global list of properties.
	 * @param builder stores method bindings.
	 * @param type is the type to inspect.
	 */
	private void parseType(Class<?> type) {		
		for (Method method : type.getDeclaredMethods()) {
			String name = method.getName();
			Named named = method.getAnnotation(Named.class);
			Class<?>[] parameterTypes = method.getParameterTypes(); 
			if (parameterTypes.length == 0) {
				String propertyName;
				if ((propertyName = propertyName(named, name, "get")) != null) {
					Property p = createProperty(propertyName);
					p.addGetter(method);
					properties.put(propertyName, p);
				}
				else if ((propertyName = propertyName(named, name, "is")) != null) {
					Property p = createProperty(propertyName);
					p.addGetter(method);
					properties.put(propertyName, p);
				}					
			}
			else if (parameterTypes.length == 1) {
				String propertyName;
				if ((propertyName = propertyName(named, name, "set")) != null) {
					Property p = createProperty(propertyName);
					p.addSetter(method);
					properties.put(propertyName, p);
				}
			}
		}
		
		for (Class<?> intf : type.getInterfaces()) {
			parseType(intf);
		}

		Class<?> sup = type.getSuperclass();
		if (sup != null) {
			parseType(sup);
		}
	}	
}
