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

import net.xclinical.iso11179.ext.FlatJsonExporter;
import net.xclinical.iso11179.ext.SimpleJsonExporter;
import net.xclinical.iso11179.ext.Visitable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

public class JsonTreeWriter implements TreeWriter {

	private final JSONObject obj;

	public JsonTreeWriter() {
		this(new JSONObject());
	}

	public JsonTreeWriter(JSONObject obj) {
		this.obj = obj;
	}
	
	public JSONObject getObject() {
		return obj;
	}

	@Override
	public ListWriter subList(String key) throws TreeException {
		JSONArray arr = new JSONArray();
		try {
			obj.put(key, arr);
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return new JsonListWriter(arr);
	}
	
	@Override
	public TreeWriter subTree(String key) throws TreeException {
		JsonTreeWriter subWriter = new JsonTreeWriter();
		try {
			obj.put(key, subWriter.obj);
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return subWriter;
	}
	
	@Override
	public TreeWriter put(String key, Key value) throws TreeException {
		try {
			obj.put(key, value.toString());
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}

	@Override
	public TreeWriter put(String key, HasKey value) throws TreeException {
		if (value != null) {
			JSONObject o = new JSONObject();
			TreeWriter inner = new JsonTreeWriter(o);
			Object entity = PMF.find(value.getKey());
			SimpleJsonExporter.copy(inner, (Visitable)entity);
			
			try {
				obj.put(key, o);
			}
			catch(JSONException e) {
				throw new TreeException(e);
			}
		}
		return this;
	}
	
	@Override
	public TreeWriter put(String key, String value) throws TreeException {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}

	@Override
	public TreeWriter put(String key, Boolean value) throws TreeException {
		try {
			if (value != null) {
				obj.put(key, value);
			}
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}
	
	@Override
	public TreeWriter put(String key, Integer value) throws TreeException {
		try {
			if (value != null) {
				obj.put(key, value);
			}
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}

	@Override
	public TreeWriter put(String key, Float value) throws TreeException {
		try {
			if (value != null) {
				obj.put(key, value);
			}
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}

	@Override
	public TreeWriter put(String key, Iterable<? extends Visitable> iter) throws TreeException {
		try {
			obj.put(key, FlatJsonExporter.of(iter));
		} catch (JSONException e) {
			throw new TreeException(e);
		}
		return this;
	}
}
