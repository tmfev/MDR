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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a property of a {@link Bean}.
 * 
 * @author michael@mictale.com
 */
public class Property {

	private final String name;

	private Class<?> type;

	private boolean canRead;

	private boolean canWrite;

	private Object defaultValue;

	private Method getter;

	private Method setter;

	Property(String name) {
		this.name = name;
	}

	private void addType(Class<?> type) {
		if (this.type != null) {
			if (type.isAssignableFrom(this.type)) {
				this.type = type;
			} else if (!this.type.isAssignableFrom(type)) {
				throw new IllegalArgumentException("Property types mismatch: Expected " + this.type
						+ " but found incompatible type " + type);
			}
		} else {
			this.type = type;
		}
	}

	void addGetter(Method m) {
		getter = m;
		addType(m.getReturnType());
	}

	void addSetter(Method m) {
		setter = m;
		addType(m.getParameterTypes()[0]);
	}

	void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	private BeanException setFailed(Object bean, Object value, Throwable cause) {
		return new BeanException(
				"Setter " + setter + " on " + bean + " could not set value " + value + value == null ? ""
						: (" of type " + value.getClass()), cause);
	}

	public void setValue(Object bean, Object value) {
		try {
			if (setter == null)
				throw new NullPointerException("No setter for " + name);
			setter.setAccessible(true);
			
			Class<?> type = setter.getParameterTypes()[0];
			if (value == null || type.isInstance(value)) {
				setter.invoke(bean, value);
			}
			else if (type.isAssignableFrom(String.class)) {
				setter.invoke(bean, String.valueOf(value));				
			}
			else {
				throw setFailed(bean, value, null);
			}
		} catch (IllegalArgumentException e) {
			throw setFailed(bean, value, e);
		} catch (IllegalAccessException e) {
			throw setFailed(bean, value, e);
		} catch (InvocationTargetException e) {
			throw setFailed(bean, value, e.getTargetException());
		}
	}

	public Object getValue(Object bean) {
		try {
			if (getter == null)
				throw new NullPointerException("No getter for " + name);
			return getter.invoke(bean);
		} catch (IllegalArgumentException e) {
			throw new BeanException(e);
		} catch (IllegalAccessException e) {
			throw new BeanException(e);
		} catch (InvocationTargetException e) {
			throw new BeanException(e);
		}
	}

	/**
	 * Retrieves the name of this property.
	 * 
	 * @return the name of the property.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the type of the property.
	 * 
	 * @return the type of this property.
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Determines if the property can be read.
	 * 
	 * @return <code>true</code> if the property can be read.
	 */
	public boolean canRead() {
		return canRead;
	}

	void setReadable(boolean readable) {
		canRead = readable;
	}

	/**
	 * Determines if the property can be changed.
	 * 
	 * @return <code>true</code> if the property can be changed.
	 */
	public boolean canWrite() {
		return canWrite;
	}

	void setWriteable(boolean writeable) {
		canWrite = writeable;
	}

	@Override
	public String toString() {
		return name;
	}
}
