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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.service.RemoteException;

/**
 * Append an item to a list.
 * 
 * @author ms@xclinical.com
 */
public class InvokeCommand extends AbstractCommand {

	private static final Logger LOG = Logger.get(InvokeCommand.class);
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {		
		List<? extends TreeSource> args = source.subList("args");
		String name = source.getString("name");

		Object[] argv = new Object[args.size()];
		
		LOG.debug("Binding parameters");
		for (int i = 0; i < args.size(); i++) {
			Object entity = TreeBinding.bind(args.get(i));
			LOG.debug("Binding {0} at index {1}", entity, i);
			argv[i] = entity;
		}
		
		int ind = name.lastIndexOf('.');
		final String clazz = name.substring(0, ind);
		final String meth = name.substring(ind + 1);

		try {
			Class<?> cls = Class.forName(clazz);
			boolean invoked = false;
			for (Method m : cls.getDeclaredMethods()) {
				if (m.getName().equals(meth)) {
					Object result = m.invoke(null, argv);
					if (result instanceof Collection<?>) {
						FlatJsonExporter.copy(writer, new ResultList((Collection)result));					
					}
					else if (result instanceof Visitable) {
						FlatJsonExporter.copy(writer, (Visitable)result);
					}
					invoked = true;
					break;					
				}
			}
			
			if (!invoked) {
				throw new IllegalArgumentException("The remote method " + name + " was not found");
			}
		} catch (ClassNotFoundException e) {
			throw new RemoteException(e);
		} catch (IllegalArgumentException e) {
			throw new RemoteException(e);
		} catch (IllegalAccessException e) {
			throw new RemoteException(e);
		} catch (InvocationTargetException e) {
			throw new RemoteException(e.getTargetException());
		}
	}
	
}
