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
import javax.persistence.Table;

import net.xclinical.iso11179.ext.IndexListener;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Datatype (11.3.2.9).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "DATA_TYPE")
@EntityListeners(IndexListener.class)
public class DataType extends Item implements HasKey, Indexable {

	private String name;

	private String description;

	private String schemeReference;

	public DataType() {
	}

	public static DataType create(String name, String description, String schemeReference) {
		DataType dataType = new DataType();
		dataType.setName(name);
		dataType.setDescription(description);
		dataType.setSchemeReference(schemeReference);

		PMF.get().persist(dataType);
		return dataType;
	}

	@Override
	public void reindex(IndexTokenReceiver receiver) {
		receiver.tokenize(name, this, this);
		receiver.tokenize(description, this, this);
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.DataType.URN);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setSchemeReference(String schemeReference) {
		this.schemeReference = schemeReference;
	}

	public String getSchemeReference() {
		return schemeReference;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
