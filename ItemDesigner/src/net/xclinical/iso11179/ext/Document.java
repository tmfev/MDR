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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import net.xclinical.iso11179.ReferenceDocument;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * A byte stream with name and type.
 *
 * Documents can be associated to {@link ReferenceDocument} by having the URI of the {@link ReferenceDocument} point
 * to an instance of {@link Document}.
 *  
 * @author ms@xclinical.com
 */
@Entity
@Table(name="DOCUMENT")
public class Document implements HasKey {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String type;

	@Column(length=1024 * 1024 * 100)
	@Lob
	private byte[] content;
	
	public Document() {
	}

	public static Document create(String name, String type, byte[] content) {
		Document document = new Document();
		document.name = name;
		document.type = type;
		document.content = content;
		
		PMF.get().persist(document);
		
		return document;
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.Document.URN, id);
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

	public void setContent(byte[] content) {
		this.content = content;
	}

	public byte[] getContent() {
		return content;
	}
}
