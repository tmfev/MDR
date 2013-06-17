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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonTreeSource implements TreeSource {

	private JSONObject obj;
	
	public JsonTreeSource(JSONObject obj) {
		this.obj = obj;
	}
	
	@Override
	public String getString(String name) {
		return obj.optString(name);
	}

	@Override
	public int getInt(String name) {
		return obj.optInt(name);
	}

	@Override
	public List<? extends TreeSource> subList(String name) throws TreeException {
		JSONObject obj = this.obj.optJSONObject(name);
		if (obj != null) {
			return Collections.singletonList(new JsonTreeSource(obj));
		}

		JSONArray arr = this.obj.optJSONArray(name);
		if (arr != null) {
			List<TreeSource> a = new ArrayList<TreeSource>();
			
			for (int i = 0; i < arr.length(); i++) {
				try {
					a.add(new JsonTreeSource(arr.getJSONObject(i)));
				}
				catch (JSONException e) {
					throw new TreeException(e);
				}
			}
			
			return a;
		}

		return Collections.emptyList();
	}

	@Override
	public TreeSource subNode(String name) throws TreeException {
		Object n = this.obj.opt(name);
		
		if (JSONObject.NULL.equals(n)) {
			return null;
		}
		else if (n instanceof JSONObject) {
			JSONObject obj = (JSONObject)n;
			return new JsonTreeSource(obj);
		}
		else {
			throw new TreeException("Expected an object while parsing " + name);
		}
	}
}
