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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Object_Class (11.2.2.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="OBJECT_CLASS")
@NamedQueries({ @NamedQuery(name = com.xclinical.mdr.client.iso11179.model.ObjectClass.CONCEPTS, query = "select c from DataElementConcept c where c.objectClass = ?1") })
public class ObjectClass extends Concept {

	@OneToMany(mappedBy="objectClass")
	private Set<DataElementConcept> concepts = new HashSet<DataElementConcept>();
	
	public ObjectClass() {
	}
	
	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.ObjectClass.URN);
	}	
	
	public static ObjectClass create(Context ctx, String sign, LanguageIdentification language) {
		ObjectClass obj = new ObjectClass();
		PMF.get().persist(obj);
		
		ctx.designate(obj, sign, language);
		return obj;
	}

	public void addConcept(DataElementConcept concept) {
		concepts.add(concept);
	}
	
	public Set<DataElementConcept> getConcepts() {
		return concepts;
	}
}
