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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Relation (9.1.2.4).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "RELATION")
@NamedQueries({
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Relation.ROLES, query = "select r from RelationRole r where r.source = ?1"),
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Relation.LINKS, query = "select l from Link l where l.relation = ?1") 
})
public class Relation extends Concept {

	@OneToMany(mappedBy = "source")
	private Collection<RelationRole> roles = new ArrayList<RelationRole>();

	@OneToMany(mappedBy = "relation")
	private Collection<Link> links = new ArrayList<Link>();

	public Relation() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Relation.URN);
	}

	public void addLink(Link link) {
		link.setRelation(this);
		links.add(link);
	}

	public void addRole(RelationRole role) {
		role.setSource(this);
		roles.add(role);
	}

	public Collection<RelationRole> getRoles() {
		return roles;
	}

	public Collection<Link> getLinks() {
		return links;
	}

	public static Relation create(Context ctx, String sign, LanguageIdentification language) {
		Relation element = new Relation();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
