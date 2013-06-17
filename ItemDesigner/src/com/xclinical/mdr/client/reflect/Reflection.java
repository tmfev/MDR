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
package com.xclinical.mdr.client.reflect;

import com.google.gwt.core.client.GWT;

/**
 * Provides services to access types.
 * 
 * @author michael@mictale.com
 *
 */
public final class Reflection {

	private static ObjectFactory f;

	/**
	 * Initializes reflection.
	 * 
	 * This method must be called prior to any call to {@link #newInstance(String)}.
	 * One might argue that it would be beneficial to have the initialization code in a
	 * static initializer which would be called automatically. The reasons for having it
	 * here explicitly are:
	 * <ul>
	 * 	<li>Initializing creates all singletons which is a lot of code o run inside a
	 * 		static initializer. Remember that it is hard to track down exceptions thrown
	 * 		from within a static initializer.</li>
	 * 	<li>The static initializer would run when the class is loaded. This might be very late,
	 * 		potentially losing other initialization code. For example, the constructor of the
	 * 		singleton class might register the singleton somewhere. This registry would remain
	 * 		empty unless someone loads {@link Reflection} explicitly.</li>
	 * </ul>
	 */
	public static void initialize() {
		if (f == null) {
			f = (ObjectFactory)GWT.create(ObjectFactory.class);
		}
	}
	
	/**
	 * Creates a new object from a type known by name.
	 * 
	 * To successfuly create an instance using this method, you must:
	 * <ol>
	 * 	<li>Add the annotation {@link Instantiable} to the class.</li>
	 *  <li>Give the class an empty public constructor or no constructor at all.</li>
	 *  <li>Make sure the class is not abstract.</li>
	 *  <li>Make sure the class is public.</li>
	 * 	<li>Add the following to your {@code gwt.xml} file.
	 * <pre><code>
	 * 	&lt;generate-with class="weed.server.gwt.FactoryGenerator" &gt;
	 *		&lt;when-type-assignable class="weed.client.reflect.ObjectFactory" /&gt;
	 *	&lt;/generate-with&gt;
	 * </code></pre>
	 *	</li>
	 * 
	 * @param <T> is the type of the object.
	 * @param className is the full class name of the object.
	 * @return the new object.
	 * @throws ReflectException if the class cannot be created.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) throws ReflectException {
		if (f == null) throw new IllegalStateException("Not initialized (forgot to call initialize()?)");
		T t = (T)f.newInstance(className);
		return t;
	}
}
