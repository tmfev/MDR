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
package com.xclinical.mdr.server.tree;

import net.xclinical.iso11179.ext.Visitable;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

public interface TreeWriter {

	ListWriter subList(String key) throws TreeException;
	
	TreeWriter subTree(String key) throws TreeException;
	
	TreeWriter put(String key, HasKey value) throws TreeException;

	TreeWriter put(String key, Key value) throws TreeException;

	TreeWriter put(String key, String value) throws TreeException;

	TreeWriter put(String key, Boolean value) throws TreeException;
	
	TreeWriter put(String key, Integer value) throws TreeException;

	TreeWriter put(String key, Float value) throws TreeException;

	TreeWriter put(String key, Iterable<? extends Visitable> iter) throws TreeException;
}
