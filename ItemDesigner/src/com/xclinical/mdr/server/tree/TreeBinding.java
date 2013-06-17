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

import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

public class TreeBinding {
	
	@SuppressWarnings("unchecked")
	public static <T> T bind(TreeSource source) {
		if (source == null) {
			return null;
		}
		else {
			String id = source.getString(Item.ID);
			Key key = Key.parse(id);
			if (key.hasValue()) {
				return (T) PMF.find(key);
			}
			else {
				return null;
			}
		}
	}
}
