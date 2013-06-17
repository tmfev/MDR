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

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Data_Element (11.1.2.4).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="DATA_ELEMENT")
public class DataElement extends Item {

	@ManyToOne(optional=true)
	private ValueDomain domain;
	
	@ManyToOne(optional=true)
	private DataElementConcept meaning;
	
	private Integer precision;
	
	public DataElement() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.DataElement.URN);
	}
	
	public Integer getPrecision() {
		return precision;
	}
	
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	
	public void setDomain(ValueDomain domain) {
		this.domain = domain;
	}

	public ValueDomain getDomain() {
		return domain;
	}
	
	void setMeaning(DataElementConcept meaning) {
		this.meaning = meaning;
	}

	public DataElementConcept getMeaning() {
		return meaning;
	}
	
	public static DataElement create(Context ctx, String sign, LanguageIdentification language) {
		DataElement element = new DataElement();
		PMF.get().persist(element);		
		ctx.designate(element, sign, language);
		return element;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
