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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.xclinical.iso11179.Item;

import com.xclinical.mdr.repository.Key;

@Entity
@Table(name="USER_GROUP")
@NamedQueries({
@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.UserGroup.ALL, query="select g from UserGroup g")
})
public class UserGroup extends Item {

	@ManyToMany(targetEntity=User.class)
	private Set<User> subjects = new HashSet<User>();
	
	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.UserGroup.URN);		
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public void addSubject(User subject) {
		subjects.add(subject);		
	}

	public void removeSubject(User subject) {
		subjects.remove(subject);		
	}
	
	public Set<User> getSubjects() {
		return subjects;
	}
}
