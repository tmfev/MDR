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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.repository.RepositoryStoreException;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.service.RemoteException;

/**
 * A helper to run named queries.
 * 
 * @author ms@xclinical.com
 */
public class SearchHelper {

	public <T> List<T> runNamedQuery(int start, String name, Object...params) {
		Query query = PMF.get().createNamedQuery(name);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}

		query.setFirstResult(start);
		
		@SuppressWarnings("unchecked")
		List<T> hits = query.getResultList();
		return hits;
	}

	public <T> List<T> extractKey(int length, List<Object[]> l) throws RemoteException {
		List<T> items = new ArrayList<T>(l.size());
		
		int count = Math.min(l.size(), length);
		for (int i = 0; i < count; i++) {
			Object[] hit = l.get(i);
			Key k = Key.parse((String)hit[0]);
			
			try {
				items.add((T) PMF.find(k));
			}
			catch(RepositoryStoreException e) {
				throw new RemoteException(e);
			}
		}		
		
		return items;
	}
	
}
