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

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.service.RemoteException;

/**
 * Load a slice of a list.
 * 
 * @author ms@xclinical.com
 */
public class LoadListCommand extends AbstractCommand {

	private static final Logger LOG = Logger.get(LoadListCommand.class);
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {		
		int start = source.getInt("start");
		int length = source.getInt("length");		
		List<? extends TreeSource> args = source.subList("args");
		String name = source.getString("name");
		String query = source.getString("query");

		Query q;
		
		if (query.length() > 0) {
			LOG.debug("Loading list {0} {1}({2})", query, start, length);
			
			q = PMF.get().createQuery(query);			
		}
		else if (name.length() > 0) {
			LOG.debug("Loading list {0} {1}({2})", name, start, length);			
			LOG.debug("Running named query {0}", name);
			q = PMF.get().createNamedQuery(name);			
		}
		else {
			LOG.debug("Returning empty list");
			FlatJsonExporter.copy(writer, new ResultList(Collections.<Object>emptyList(), 0, 0));
			return;
		}
		
		LOG.debug("Binding parameters");
		for (int i = 0; i < args.size(); i++) {
			Object entity = TreeBinding.bind(args.get(i));
			LOG.debug("Binding {0} at index {1}", entity, i);			
			q.setParameter(i + 1, entity);
		}
		
		q.setFirstResult(start);
		// q.setMaxResults(length);
		
		@SuppressWarnings("unchecked")
		List<Object> res = q.getResultList();

		int last = start + res.size();
		FlatJsonExporter.copy(writer, new ResultList(res.subList(start, Math.min(last, start + length)), start, last));
	}
	
}
