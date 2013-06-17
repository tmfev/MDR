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
package com.xclinical.mdr.util;

import java.util.HashMap;
import java.util.Set;

public class HashTree<N> {

	private final Node<N> root;
	
	public HashTree() {
		this(new Node<N>());
	}
	
	private HashTree(Node<N> root) {
		this.root = root;
	}
	
	public void put(N... nodes) {
		Node<N> n = root;
		
		for (int i = 0; i < nodes.length; i++) {
			Node<N> next = n.map.get(nodes[i]);
			if (next == null) {
				next = new Node<N>();
				n.map.put(nodes[i], next);
			}
			
			n = next;
		}
	}

	public Set<N> get(N... nodes) {
		Node<N> n = root;
		
		for (int i = 0; i < nodes.length; i++) {
			n = n.map.get(nodes[i]);			
		}
		
		return n.map.keySet();		
	}
	
	public HashTree<N> subTree(N... nodes) {
		Node<N> n = root;
		
		for (int i = 0; i < nodes.length; i++) {
			n = n.map.get(nodes[i]);			
		}
		
		return new HashTree<N>(n);		
	}
	
	private static class Node<N> {
		private final HashMap<N, Node<N>> map = new HashMap<N, Node<N>>();
	}
}
