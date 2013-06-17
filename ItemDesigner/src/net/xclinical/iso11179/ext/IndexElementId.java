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

import java.io.Serializable;

public class IndexElementId implements Serializable {

	private static final long serialVersionUID = -2986471511736781784L;

	private String token;

	private String element;

	private String type;

	public IndexElementId(String token, String element, String type) {
		this.token = token;
		this.element = element;
		this.type = type;
	}
	
	public IndexElementId() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IndexElementId) {
			IndexElementId other = (IndexElementId) obj;
			return other.token.equals(token) && other.element.equals(element) && other.type.equals(type);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int) (token.hashCode() ^ element.hashCode() ^ type.hashCode());
	}

	@Override
	public String toString() {
		return "IndexElementId{token=" + token + ", element=" + element + ", type=" + type + "}";
	}
}
