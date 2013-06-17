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
package net.xclinical.iso11179;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;

/**
 * Represents a Link (9.1.2.6).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="LINK")
public class Link extends Assertion {

	@ManyToOne
	private Relation relation;

	public Link() {		
	}
	
	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Link.URN);
	}
	
	void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public Relation getRelation() {
		return relation;
	}
}
