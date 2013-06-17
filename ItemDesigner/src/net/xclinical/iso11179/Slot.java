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
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.IndexListener;
import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;

/**
 * Represents a Slot (7.2.2.4).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="SLOT")
@EntityListeners(IndexListener.class)
public class Slot implements HasKey, Indexable, Visitable {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String type;
	
	private String value;
	
	public Slot() {
	}

	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.Slot.URN, id);
	}
	
	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(name, this, this);
		receiver.tokenize(type, this, this);
		receiver.tokenize(value, this, this);		
	}
	
	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
