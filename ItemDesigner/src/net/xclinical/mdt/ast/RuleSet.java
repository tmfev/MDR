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
package net.xclinical.mdt.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RuleSet implements Iterable<Rule> {

	private final List<Rule> rules = new LinkedList<Rule>();
	
	public void add(Rule rule) {
		rules.add(rule);
	}
	
	public int size() {
		return rules.size();
	}
	
	@Override
	public Iterator<Rule> iterator() {
		return rules.iterator();
	}
}
