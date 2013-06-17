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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

/**
 * Represents a Classification association class (9.2.3.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="CLASSIFICATION")
public class Classification implements HasKey, Visitable {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(optional=true)
	private Item classifiedItem;
	
	@ManyToOne(optional=true)
	private Concept classifier;

	@ManyToMany(targetEntity=ConceptSystem.class)
	private List<ConceptSystem> schemes = new ArrayList<ConceptSystem>();
	
	public Classification() {
	}

	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.Classification.URN, id);
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
	
	public void setClassifiedItem(Item classifiedItem) {
		this.classifiedItem = classifiedItem;
	}
	
	public ClassifiableItem getClassifiedItem() {
		return classifiedItem;
	}
	
	public void setClassifier(Concept classifier) {
		this.classifier = classifier;
	}
	
	public Concept getClassifier() {
		return classifier;
	}
	
	public List<ConceptSystem> getSchemes() {
		return schemes;
	}
	
	public void addScheme(ConceptSystem scheme) {
		schemes.add(scheme);
	}
}
