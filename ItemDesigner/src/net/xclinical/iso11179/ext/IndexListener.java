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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Query;

import net.xclinical.iso11179.TypeNotFoundException;
import net.xclinical.iso11179.Types;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.IndexTokenReceiver;
import com.xclinical.mdr.repository.Indexable;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

public class IndexListener {

	private static final Logger log = Logger.get(IndexListener.class);
	
	private static String getUrn(Class<?> type) {
		return "urn:mdr:" + type.getSimpleName();
	}

	private static List<String> getSuperTypes(String urn) throws TypeNotFoundException {
		List<String> superTypes = new ArrayList<String>();

		Class<?> type = Types.getType(urn);
		while (type != null && type.isAnnotationPresent(Entity.class)) {
			superTypes.add(getUrn(type));
			type = type.getSuperclass();
		}

		return superTypes;
	}

	private static void addIndex(EntityManager em, String token, Key element, Key target, List<String> types) {
		for (String type : types) {
			IndexElement find = em.find(IndexElement.class, new IndexElementId(token, element.toString(), type));
			if (find == null) {
				IndexElement elm = IndexElement.create(token, element, target, type);
				em.persist(elm);					
			}
		}
	}

	private static class Indexer implements IndexTokenReceiver {
		private EntityManager em;

		Indexer(EntityManager em) {
			this.em = em;
		}

		@Override
		public void tokenize(String token, HasKey element, HasKey target) {
			tokenize(token, element, target, EnumSet.noneOf(Options.class));
		}

		public void tokenize(String token, HasKey element, HasKey target, EnumSet<Options> options) {
			if (token != null) {
				if (element == null)
					throw new NullPointerException();

				if (token != null) {
					Key targetKey = target == null ? element.getKey() : target.getKey();

					try {
						List<String> types = getSuperTypes(targetKey.getName());

						if (options.contains(Options.NEVER_SPLIT)) {
							addIndex(em, token, element.getKey(), targetKey, types);
						} else {
							// TODO: Remove hard-coded locale.
							BreakIterator boundary = BreakIterator.getWordInstance(Locale.GERMAN);

							boundary.setText(token);
							int start = boundary.first();
							for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
									.next()) {
								String part = token.substring(start, end).trim();

								if (part.length() > 0) {
									addIndex(em, part, element.getKey(), targetKey, types);
								}
							}
						}
					} catch (TypeNotFoundException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		};
	};

	private static void reindex(Indexable entity) {
		EntityManager em = PMF.getFactory(PMF.INDEX_NAME).createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Query query = em.createNamedQuery(IndexElement.REMOVE_BY_ELEMENT);
			query.setParameter(1, entity.getKey().toString());
			query.executeUpdate();

			entity.reindex(new Indexer(em));

			tx.commit();
		} catch (Exception e) {
			log.severe(e);
			tx.rollback();
		} finally {
			em.close();
		}
	}

	@PostUpdate
	public void tokenize(Object entity) {
		log.debug("Running post update on {0}", entity);
		reindex((Indexable) entity);
	}

	@PostPersist
	public void tokenize2(Object entity) {
		log.debug("Running post persist on {0}", entity);
		reindex((Indexable) entity);
	}
}
