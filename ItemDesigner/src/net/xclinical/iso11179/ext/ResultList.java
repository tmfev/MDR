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
package net.xclinical.iso11179.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ResultList implements Visitable {

	private List<Object> elements;

	private int start;

	private int length;

	public ResultList(List<Object> elements, int start, int length) {
		this.elements = elements;
		this.start = start;
		this.length = length;
	}

	public ResultList(Collection<? extends Object> elements) {
		this.elements = new ArrayList<Object>(elements);
		this.start = 0;
		this.length = elements.size();
	}
	
	public int getStart() {
		return start;
	}
	
	public int getLength() {
		return length;
	}
	
	public List<Object> getElements() {
		return elements;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
