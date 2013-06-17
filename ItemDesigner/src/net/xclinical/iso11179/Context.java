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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.repository.RepositoryStoreException;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

/**
 * Represents an Context (7.3.2.5).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "CONTEXT")
@NamedQueries({ 
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Context.RELEVANT_DESIGNATIONS, 
			query = "select distinct d.designation.item, d.designation.sign from DesignationContext d where d.context = ?1 order by lower(d.designation.sign)") 
})
public class Context extends Item {

	private static final Logger LOG = Logger.get(Context.class);
	
	public static final String MDR = "MDR";

	@OneToMany(mappedBy = "context", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Set<DesignationContext> relevantDesignations = new HashSet<DesignationContext>();

	@OneToMany(mappedBy = "context", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Set<DefinitionContext> relevantDefinitions = new HashSet<DefinitionContext>();

	public Context() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Context.URN);
	}

	public Collection<DesignationContext> getRelevantDesignations() {
		return relevantDesignations;
	}

	public void addRelevantDesignation(DesignationContext designationContext) {

		relevantDesignations.add(designationContext);
	}

	public void removeRelevantDesignation(DesignationContext designationContext) {
		relevantDesignations.remove(designationContext);
		PMF.get().remove(designationContext);
	}

	public Set<DefinitionContext> getRelevantDefinitions() {
		return relevantDefinitions;
	}

	public void addRelevantDefinition(DefinitionContext definitionContext) {
		relevantDefinitions.add(definitionContext);
	}

	public Designation getRelevantDesignation(String sign) {
		for (DesignationContext d : relevantDesignations) {
			Designation des = d.getDesignation();
			if (sign.equals(des.getSign())) {
				return des;
			}
		}

		throw new IllegalArgumentException("No such designation: " + sign);
	}

	public DesignationContext addRelevantDesignation(Designation designation) {
		DesignationContext designationContext = DesignationContext.create(designation, this);
		return designationContext;
	}

	public Designation designate(DesignatableItem designatableItem, String sign, LanguageIdentification language) {
		Designation designation = new Designation(sign, language);
		PMF.get().persist(designation);

		designatableItem.addDesignation(designation);
		addRelevantDesignation(designation);
		return designation;
	}

	public Definition define(DesignatableItem designatableItem, String text) {
		Definition definition = new Definition(text);
		PMF.get().persist(definition);

		designatableItem.addDefinition(definition);
		addRelevantDefinition(definition);
		return definition;
	}

	public Definition find(String text) {
		Query query = PMF.get()
				.createQuery("select d.definition from DefinitionContext d where d.context=? and d.definition.text=?")
				.setParameter(1, this).setParameter(2, text);

		try {
			return (Definition) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public DefinitionContext addRelevantDefinition(Definition definition) {
		DefinitionContext definitionContext = DefinitionContext.create(definition, this);
		return definitionContext;
	}

	public static Context create(String sign, LanguageIdentification language) throws RepositoryStoreException {
		Context context = new Context();
		PMF.get().persist(context);

		context.designate(context, sign, language);
		return context;
	}

	public static Context create(Context parent, String sign, LanguageIdentification language)
			throws RepositoryStoreException {
		Context context = new Context();
		PMF.get().persist(context);

		parent.designate(context, sign, language);
		return context;
	}

	@Override
	public void addChild(Item item) {
		if (item == null)
			throw new NullPointerException();

		for (Designation designation : item.getDesignations()) {
			addRelevantDesignation(designation);
		}
	}

	public void removeChild(Item item) {
		if (item == null)
			throw new NullPointerException();

		Query query = PMF.get().createQuery(
				"select c from DesignationContext c where c.context = ? and c.designation.item = ?");
		query.setParameter(1, this);
		query.setParameter(2, item);

		List<DesignationContext> resultList = query.getResultList();
		for (DesignationContext ctx : resultList) {
			relevantDesignations.remove(ctx);
			PMF.get().remove(ctx);
		}
	};

	public void accept(Visitor visitor) {
		visitor.visit(this);
	};

	public static void initializeRoot() {
		LOG.info("Initializing root context");

		Context root;
		
		try {
			root = root();
			LOG.info("Existing root context found: " + root.getKey());
		} catch (NoResultException e) {
			LOG.info("No root context found, creating new");
			root = Context.create(Context.MDR, LanguageIdentification.findOrCreate(LanguageIdentification.EN_US));
			LOG.info("Created root context: " + root.getKey());
		}
		
		LOG.info("Root context is: " + root.getKey());
	}
	
	public static Context root() {
		Query query = PMF.get().createQuery(
				"select c.context from DesignationContext c where c.context = c.designation.item");

		return (Context) query.getSingleResult();
	}
}
