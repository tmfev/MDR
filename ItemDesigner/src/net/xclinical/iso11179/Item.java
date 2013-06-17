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
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Rating;
import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.xclinical.mdr.client.iso11179.model.AbstractDesingatableItem;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

/**
 * The superclass of most entities, both a {@link DesignatableItem} and an {@link IdentifiedItem}.
 * 
 * @author ms@xclinical.com
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "BASE_ITEM")
@NamedQueries({
@NamedQuery(name=AbstractDesingatableItem.DESIGNATIONS, query="select d from Designation d where d.item = ?1"),
@NamedQuery(name=AbstractDesingatableItem.DEFINITIONS, query="select d from Definition d where d.item = ?1"),
@NamedQuery(name=AbstractDesingatableItem.CLASSIFIER, query="select c.classifier from Classification c where c.classifiedItem = ?1"),
@NamedQuery(name=AbstractDesingatableItem.REMOVE_CLASSIFIERS, query="delete from Classification c where c.classifiedItem = ?1 and c.classifier = ?2"),

@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_RATING, 
	query="select distinct e.target, (select coalesce(avg(value), 0) from Vote v where target = e.target) as av from IndexElement e where upper(e.token) like ?1 order by av desc"),
@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_RATING_AND_TYPE, 
	query="select distinct e.target, (select coalesce(avg(value), 0) from Vote v where target = e.target) as av from IndexElement e where upper(e.token) like ?1 and e.type = ?2 order by av desc"),

@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_REFERENCE, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='favourites') as av from IndexElement e where upper(e.token) like ?1 order by av desc"),
@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_REFERENCE_AND_TYPE, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='favourites') as av from IndexElement e where upper(e.token) like ?1 and e.type = ?2 order by av desc"),

@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_CLICKS, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='history') as av from IndexElement e where upper(e.token) like ?1 order by av desc"),
@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_CLICKS_AND_TYPE, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='history') as av from IndexElement e where upper(e.token) like ?1 and e.type = ?2 order by av desc"),

@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_UPDATES, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='update') as av from IndexElement e where upper(e.token) like ?1 order by av desc"),
@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.Item.SEARCH_BY_UPDATES_AND_TYPE, 
	query="select distinct e.target, (select count(*) from ItemLink l where l.target = e.target and l.tag='update') as av from IndexElement e where upper(e.token) like ?1 and e.type = ?2 order by av desc")
})
public abstract class Item extends AbstractEntity implements DesignatableItem, IdentifiedItem, ClassifiableItem, Visitable {

	private static final Logger LOG = Logger.get(Item.class);
	
	@OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
	private Collection<Designation> designations = new ArrayList<Designation>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
	private Collection<Definition> definitions = new ArrayList<Definition>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
	private Collection<ScopedIdentifier> identifiers = new ArrayList<ScopedIdentifier>();

	@OneToMany(mappedBy = "classifiedItem", cascade = CascadeType.PERSIST)
	private Collection<Classification> classifications = new ArrayList<Classification>();
	
	public Item() {
	}

	public static void appendClassifier(Item item, Item classifier) {
		if (classifier instanceof Concept) {
			Classification classification = new Classification();
			classification.setClassifiedItem(item);
			classification.setClassifier((Concept)classifier);
			PMF.get().persist(classification);
		}
		else {
			throw new IllegalArgumentException("Specified item is not a Concept");
		}
	}

	public static void removeClassifier(Item item, Item classifier) {
		Query query = PMF.get().createNamedQuery(AbstractDesingatableItem.REMOVE_CLASSIFIERS);
		query.setParameter(1, item);
		query.setParameter(2, classifier);
		query.executeUpdate();
	}
	
	public Rating rate(Rating rating) {
		return Rating.rate(rating);
	}

	@Override
	public void addClassification(Classification classification) {
		classification.setClassifiedItem(this);
		classifications.add(classification);
	}
	
	@Override
	public Collection<Classification> getClassifications() {
		return classifications;
	}
	
	public void addDesignation(Designation designation) {
		designation.setItem(this);
		designations.add(designation);
	}

	public void removeDesignation(Designation designation) {
		designation.setItem(null);
		designations.remove(designation);
	}

	public Collection<Designation> getDesignations() {
		return designations;
	}

	public boolean hasDesignations() {
		return !designations.isEmpty();
	}
	
	public Designation getFirstDesignation() {
		Iterator<Designation> i = designations.iterator();
		if (i.hasNext()) {
			return i.next();
		}
		else {
			return null;
		}
	}
	
	public void addDefinition(Definition definition) {
		definition.setItem(this);
		definitions.add(definition);
	}

	public Collection<Definition> getDefinitions() {
		return definitions;
	}

	@Override
	public void setScopedIdentifier(ScopedIdentifier identifier) {
		identifier.setItem(this);
		identifiers.add(identifier);
	}

	@Override
	public void addScopedIdentifier(ScopedIdentifier identifier) {
		identifier.setItem(this);
		identifiers.add(identifier);
	}

	@Override
	public Collection<ScopedIdentifier> getScopedIdentifiers() {
		return identifiers;
	}

	public ScopedIdentifier getScopedIdentifier(Namespace ns) {
		for (ScopedIdentifier id : identifiers) {
			if (id.getScope().equals(ns)) {
				return id;
			}
		}

		return null;
	}

	public void addChild(Item item) {
		throw new UnsupportedOperationException("You cannot add child '" + item.getKey() + " to " + getKey());
	}

	public void removeChild(Item item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasKey) {
			return getKey().equals(((HasKey) obj).getKey());
		} else {
			return false;
		}
	}

	public static IdentifiedItem find(Namespace ns, String identifier) {
		LOG.debug("Looking for identifier ''{0}'' in namespace ''{1}''", identifier, ns.getNamespaceSchemeReference());
		
		Query query = PMF.get().createQuery("select i.item from ScopedIdentifier i where i.scope=? and i.identifier=?")
				.setParameter(1, ns).setParameter(2, identifier);

		List resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return (IdentifiedItem) resultList.get(0);
	}

	public abstract void accept(Visitor visitor);	
}
