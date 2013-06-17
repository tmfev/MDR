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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.mictale.jsonite.stream.JsonVisitor;

/**
 * A mutable list of JSON values.
 *
 * {@link JsonArray} represents a cursor over itself: use {@link #hasNext()} and {@link #next()} to
 * iterate over the array and {@link #reset()} to reset the cursor.
 * 
 * @author michael@mictale.com
 */
public final class JsonArray extends JsonValue implements List<JsonValue> {

	/**
	 * Represents a {@link JsonArray} that is empty.
	 */
	public static final JsonArray EMPTY = new JsonArray(Collections.<JsonValue>emptyList());
	
	private final List<JsonValue> elements;

	private int position;
	
	private JsonArray(List<JsonValue> elements) {
		this.elements = elements;
	}

	/**
	 * Initializes a new array that is initially empty.
	 */
	public JsonArray() {
		this(new ArrayList<JsonValue>());
	}

	/**
	 * Initializes an array with the specified initial capacity.
	 * 
	 * @param initialCapacity is the initial capacity.
	 */
	private JsonArray(int initialCapacity) {
		this(new ArrayList<JsonValue>(initialCapacity));
	}

	/**
	 * Initializes an array that contains the specified initial values.
	 * 
	 * @param a contains the initial values.
	 */
	private JsonArray(JsonValue[] a) {
		this(new ArrayList<JsonValue>(Arrays.asList(a)));		
	}

	/**
	 * Creates a new array initialized with the specified elements.
	 * 
	 * @param elements contains the initial elements.
	 * @return the array.
	 */
	public static JsonArray of(JsonValue...elements) {
		return new JsonArray(elements);
	}

	/**
	 * Produces a new array from the specified objects.
	 * 
	 * The effect will be identical to creating an empty array and adding the
	 * values one after the other using {@link JsonValue#of(Object)}.
	 * 
	 * @param elements are the elements in the new array.
	 * @return the new array.
	 */
	public static JsonArray of(Object...elements) {
		JsonArray arr = new JsonArray(elements.length);
		for (Object elm : elements) {
			arr.add(of(elm));
		}
		return arr;
	}

	/**
	 * Produces a new array from the specified objects.
	 * 
	 * The effect will be identical to creating an empty array and adding the
	 * values one after the other using {@link JsonValue#of(Object)}.
	 * 
	 * @param elements are the elements in the new array.
	 * @return the new array.
	 */
	public static JsonArray of(Collection<?> elements) {
		JsonArray arr = new JsonArray(elements.size());
		for (Object elm : elements) {
			arr.add(of(elm));
		}
		return arr;
	}
	
	/**
	 * Resets the current position to the beginning of the array.
	 */
	public void rewind() {
		position = 0;
	}
	
	/**
	 * Returns the current element and advances the position.
	 * 
	 * @return the current element.
	 */
	public JsonValue next() {
		return elements.get(position++);
	}

	/**
	 * Checks if the current position is within the bounds of the array. 
	 * @return <code>true</code> if {@link #next()} will return an element.
	 */
	public boolean hasNext() {
		return position < elements.size();
	}

	/**
	 * Returns <code>true</code>.
	 */
	@Override
	public boolean isArray() {
		return true;
	}
	
	@Override
	public JsonArray asArray() {
		return this;
	}
	
	public JsonValue get(int index) {
		return elements.get(index);
	}

	public void push(JsonValue value) {
		elements.add(value);
	}
	
	public int length() {
		return elements.size();
	}
	
	@Override
	public Iterator<JsonValue> iterator() {
		return new Iter();
	}

	private class Iter implements Iterator<JsonValue> {

		private int index = -1;
		
		@Override
		public boolean hasNext() {
			return index < length() - 1;
		}

		@Override
		public JsonValue next() {
			return get(++index);
		}

		@Override
		public void remove() {
			elements.remove(index);
		}		
	}

	@Override
	public boolean add(JsonValue e) {
		return elements.add(e);
	}

	@Override
	public void add(int index, JsonValue element) {
		elements.add(index, element);
		
	}

	@Override
	public boolean addAll(Collection<? extends JsonValue> c) {
		return elements.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends JsonValue> c) {
		return elements.addAll(index, c);
	}

	@Override
	public void clear() {
		elements.clear();
	}

	@Override
	public boolean contains(Object o) {
		return elements.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	@Override
	public ListIterator<JsonValue> listIterator() {
		return elements.listIterator();
	}

	@Override
	public ListIterator<JsonValue> listIterator(int index) {
		return elements.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return elements.remove(o);
	}

	@Override
	public JsonValue remove(int index) {
		return elements.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	@Override
	public JsonValue set(int index, JsonValue element) {
		return elements.set(index, element);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public List<JsonValue> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

	@Override
	public JsonType getType() {
		return JsonType.ARRAY;
	}
	
	@Override
	public void accept(JsonVisitor visitor) {
		visitor.visit(this);
	}	
}
