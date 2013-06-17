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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

@Entity
@Table(name="INDEX_ELEMENT")
@IdClass(IndexElementId.class)
@NamedQueries({ 
	@NamedQuery(name = IndexElement.REMOVE_BY_ELEMENT, query = "delete IndexElement where element=?1") 
})
public class IndexElement {

	public static final String REMOVE_ALL = "IndexElement.removeAll";

	public static final String REMOVE_BY_ELEMENT = "IndexElement.removeByElement";
	
	@Id
	@Column(length=4000)
	private String token;

	@Id
	private String element;

	@Id
	private String type;
	
	private String target;
	
	public IndexElement() {
	}

	public static IndexElement create(String token, HasKey element, HasKey target, String type) {
		IndexElement elm = new IndexElement();
		elm.token = token;
		elm.element = element.getKey().toString();
		
		Key t = target.getKey();
		elm.target = t.toString();
		elm.type = type;
		
		return elm;
	}

	public String getToken() {
		return token;
	}

	public Key getElement() {
		return Key.parse(element);
	}
	
	public Key getTarget() {
		return Key.parse(target);
	}
	
	public String getType() {
		return type;
	}	
}
