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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents an Relation_Role (9.1.2.5).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="RELATION_ROLE")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.RelationRole.LINK_ENDS, query = "select distinct r.linkEnds from RelationRole r where r = ?1") 
})
public class RelationRole extends Concept {
	
	@ManyToOne
	private Relation source;

	@ManyToMany(targetEntity=LinkEnd.class)
	private Collection<LinkEnd> linkEnds = new ArrayList<LinkEnd>();
		
	public RelationRole() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.RelationRole.URN);
	}
	
	void setSource(Relation source) {
		this.source = source;
	}
	
	public Relation getSource() {
		return source;
	}
	
	public Collection<LinkEnd> getLinkEnds() {
		return linkEnds;
	}

	void addLinkEnd(LinkEnd linkEnd) {
		linkEnds.add(linkEnd);
	}
	
	public static RelationRole create(Context ctx, String sign, LanguageIdentification language) {
		RelationRole element = new RelationRole();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
