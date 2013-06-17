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

import java.util.ListIterator;
import com.google.gwt.core.client.JavaScriptObject;

public class JsListIterator<T extends JavaScriptObject> implements ListIterator<T> {
	private final JsList<T> list;
	private int index = 0;

	public JsListIterator(JsList<T> list) {
		this.list = list;
	}

	public JsListIterator(JsList<T> list, int index) {
		this.list = list;
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		return index < list.size() - 1;
	}

	@Override
	public T next() {
		if (index < list.size() - 1) {
			++index;
			return list.get(index);
		}
		return null;
	};

	@Override
	public void remove() {
		if (index < list.size())
			list.remove(index);
	};

	@Override
	public void add(T arg0) {
		list.add(index, arg0);
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public int nextIndex() {
		return index + 1;
	}

	@Override
	public T previous() {
		if (index > 1) {
			--index;
			return list.get(index);
		}
		return null;
	}

	@Override
	public int previousIndex() {
		if (index > 0)
			return index - 1;
		return 0;
	}

	@Override
	public void set(T arg0) {
		list.set(index, arg0);
	}
}
