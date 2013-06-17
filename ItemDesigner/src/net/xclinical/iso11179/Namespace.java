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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Namespace (7.2.2.3).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "NAMESPACE")
public class Namespace extends Item {

	public static final String DATA = "urn:mdr:data";

	// naming_authority

	private Boolean oneNamePerItem;

	private Boolean oneItemPerName;

	private Boolean mandatoryNamingConvention;

	private String shorthandPrefix;

	private String namespaceSchemeReference;

	public Namespace() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Namespace.URN);
	}

	public static Namespace create(String namespaceSchemeReference, boolean oneNamePerItem, boolean oneItemPerName) {
		Namespace namespace = new Namespace();
		namespace.setNamespaceSchemeReference(namespaceSchemeReference);
		namespace.setOneNamePerItem(oneNamePerItem);
		namespace.setOneItemPerName(oneItemPerName);

		PMF.get().persist(namespace);
		return namespace;
	}

	public static Namespace findOrCreate(String namespaceSchemeReference) {
		Query query = PMF.get().createQuery("select n from Namespace n where n.namespaceSchemeReference = ?")
				.setParameter(1, namespaceSchemeReference);

		try {
			return (Namespace) query.getSingleResult();
		} catch (NoResultException e) {
			return create(namespaceSchemeReference, true, true);
		}
	}

	public Boolean getOneNamePerItem() {
		return oneNamePerItem;
	}

	public void setOneNamePerItem(Boolean oneNamePerItem) {
		this.oneNamePerItem = oneNamePerItem;
	}

	public Boolean getOneItemPerName() {
		return oneItemPerName;
	}

	public void setOneItemPerName(Boolean oneItemPerName) {
		this.oneItemPerName = oneItemPerName;
	}

	public Boolean getMandatoryNamingConvention() {
		return mandatoryNamingConvention;
	}

	public void setMandatoryNamingConvention(Boolean mandatoryNamingConvention) {
		this.mandatoryNamingConvention = mandatoryNamingConvention;
	}

	public String getShorthandPrefix() {
		return shorthandPrefix;
	}

	public void setShorthandPrefix(String shorthandPrefix) {
		this.shorthandPrefix = shorthandPrefix;
	}

	public String getNamespaceSchemeReference() {
		return namespaceSchemeReference;
	}

	public void setNamespaceSchemeReference(String namespaceSchemeReference) {
		this.namespaceSchemeReference = namespaceSchemeReference;
	}

	public static Namespace find(String schemeReference) {
		Query query = PMF.get().createQuery("select n from Namespace n where n.namespaceSchemeReference = ?")
				.setParameter(1, schemeReference);

		List resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return (Namespace) resultList.get(0);
	}

	@SuppressWarnings("unchecked")
	public static Collection<Namespace> findAll() {
		Query query = PMF.get().createQuery("select n from Namespace n");
		return query.getResultList();
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}	
}
