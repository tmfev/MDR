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
package com.xclinical.mdr.client.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A minimal but useful {@link List} implementation for {@link JsArray}.
 * 
 * @author michael@mictale.com
 *
 * @param <T> is the element type.
 */
public final class JsList<T extends JavaScriptObject> implements List<T> {
	
	private final JsArray<T> arr;
	
	public JsList(JsArray<T> arr) {
		if (arr == null) throw new NullPointerException();
		this.arr = arr;
	}

	public JsArray<T> asArray() {
		return arr;
	}

	public native JsArray<T> splice(int fromIndex, int length) /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr.splice(fromIndex, length);
	}-*/;
	
	@Override
	public boolean add(T e) {
		arr.push(e);
		return true;
	};

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) {
			add(t);
		}
		return c.size() > 0;
	}

	@Override
	public void clear() {
		arr.setLength(0);
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public native boolean isEmpty() /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr.length == 0;
	}-*/;

	@Override
	public Iterator<T> iterator() {
		return new Iter();
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index == -1) {
			return false;
		} else {
			remove(index);
			return true;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean res = false;
		for (Object obj : c) {
			boolean b = remove(obj);
			res |= b;
		}
		return res;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public native int size() /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr.length;
	}-*/;

	@Override
	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a) {
		for (int i = 0; i < size(); i++) {
			a[i] = (E)get(i);
		}
		return a;
	}

	public native T get(int index) /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr[index];
	}-*/;

	@Override
	public native T remove(int index) /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr.splice(index, 1)[0];
	}-*/;

	public native int indexOf(Object o) /*-{
		return this.@com.xclinical.mdr.client.util.JsList::arr.indexOf(o);
	}-*/;

	private class Iter implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			int siz = size();
			return index < siz;
		}

		@Override
		public T next() {
			int siz = size();
			if (index < siz) {
				return get(index++);
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size() - 1; i >= 0; i--) {
			if (o == get(i)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public native T set(int index, T element) /*-{
		var prev = this.@com.xclinical.mdr.client.util.JsList::arr[index];
		this.@com.xclinical.mdr.client.util.JsList::arr[index] = t;		
		return prev;
	}-*/;

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new JsList<T>(splice(fromIndex, toIndex - fromIndex));
	}
}
