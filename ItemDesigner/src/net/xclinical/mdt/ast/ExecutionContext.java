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
package net.xclinical.mdt.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import net.xclinical.mdt.Log;

import org.w3c.dom.Node;

import com.xclinical.mdr.server.PMF;

public class ExecutionContext {

	private final ExecutionContext parent;
	
	private final Scope scope;
	
	private Node node;
	
	private final Map<String, Reference> variables = new HashMap<String, Reference>();
	
	private ExecutionContext(ExecutionContext parent, Scope scope, Node node) {
		this.parent = parent;
		this.scope = scope;
		this.node = node;
	}
	
	public static ExecutionContext newContext(Node node) {
		return new ExecutionContext(null, null, node);
	}
	
	public ExecutionContext push(Scope scope) {
		return new ExecutionContext(this, scope, node);
	}
	
	public Scope scope() {
		return scope;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
	public void addVariable(String name, Reference value) {
		Log.m("adding variable %s initialized to %s in scope %s", name, value, this);
		variables.put(name, value);
	}

	public void setVariable(String name, Reference value) {
		scope.setVariable(name, value, this);
	}

	public <T> T getVariable(String name, Class<T> type) {
		return scope.getVariable(name, type, this);
	}
	
	public void writeVariable(String name, Reference value) {
		if (variables.containsKey(name)) {
			Log.m("setting variable %s to %s in scope %s", name, value, this);
			variables.put(name, value);
		} else {
			if (parent == null) {
				throw new IllegalArgumentException("The variable " + name + " is not defined");
			}
			parent.scope.setVariable(name, value, parent);
		}
	}

	public <T> T readVariable(String name, Class<T> type) {
		if (variables.containsKey(name)) {
			return type.cast(variables.get(name).get(this));
		} else {
			if (parent == null || parent.scope == null) {
				throw new IllegalArgumentException("No such property: '" + name + "'");
			}
			return parent.scope.getVariable(name, type, parent);
		}
	}

	public Collection<String> getVariableNames() {
		return variables.keySet();
	}

	public final Object newInstance(String identifier) {
		Class<?> clazz = scope.getVariable(identifier, Class.class, this);

		try {
			Object result = clazz.newInstance();
			if (result.getClass().isAnnotationPresent(Entity.class)) {
				PMF.get().persist(result);
			}
			return result;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not create instance of " + clazz.getName(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not create instance of " + clazz.getName(), e);
		}
	}
	
}
