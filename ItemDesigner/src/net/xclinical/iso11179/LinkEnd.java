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
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Link_End association class (9.1.4.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "LINK_END")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.LinkEnd.END_ROLES, query = "select distinct e.endRoles from LinkEnd e where e = ?1") 
})
public class LinkEnd extends AssertionEnd {

	@ManyToMany(targetEntity=RelationRole.class, mappedBy="linkEnds")
	private Collection<RelationRole> endRoles = new ArrayList<RelationRole>();
	
	public LinkEnd() {
	}

	private LinkEnd(Concept end, Link link) {
		super(end, link);
	}
	
	@Override
	public Key getKey() {
		return Key.create(com.xclinical.mdr.client.iso11179.model.LinkEnd.URN, getId());
	}
	
	public static LinkEnd create(Concept end, Link link, RelationRole endRole) {
		LinkEnd linkEnd = new LinkEnd(end, link);
		PMF.get().persist(linkEnd);
		linkEnd.addEndRole(endRole);
		link.addTerm(linkEnd);
		PMF.get().persist(endRole);
		return linkEnd;
	}	

	public Collection<RelationRole> getEndRoles() {
		return endRoles;
	}

	public void addEndRole(RelationRole endRole) {
		endRole.addLinkEnd(this);
		this.endRoles.add(endRole);		
	}
}
