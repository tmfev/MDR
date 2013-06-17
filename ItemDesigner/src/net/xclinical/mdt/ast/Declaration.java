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

import java.io.PrintStream;

import net.xclinical.mdt.Log;

import com.xclinical.mdr.server.util.Bean;
import com.xclinical.mdr.server.util.Beans;
import com.xclinical.mdr.server.util.Property;

public class Declaration extends Scope implements Reference, Statement {

	private String identifier;

	public Object instance;

	public Reference coalesce;

	public Block block = new Block();
	
	public Declaration() {
	}

	public Block getBlock() {
		return block;
	}
	
	public void setCoalesce(Reference coalesce) {
		this.coalesce = coalesce;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Object get(ExecutionContext context) {
		return instance;
	}

	@Override
	public <T> T getVariable(String name, Class<T> type, ExecutionContext context) {
		if (instance == null) {
			// Coalesce state
			return super.getVariable(name, type, context);
		}
		else {
			if (Scope.THIS.equals(name)) {
				return type.cast(instance);
			}
			else if (name.equals(identifier)) {
				return type.cast(instance);
			}
			else {
				Bean bean = Beans.fromType(instance.getClass());
				Property prop = bean.getProperty(name);
				if (prop != null) {
					return type.cast(prop.getValue(instance));
				} else {
					return super.getVariable(name, type, context);
				}
			}
		}
	}

	@Override
	public void setVariable(String name, Reference value, ExecutionContext context) {
		Bean bean = Beans.fromType(instance.getClass());
		Property prop = bean.getProperty(name);
		if (prop != null) {
			Object v = value.get(context);
			Log.m("setting property %s of bean %s to value %s", name, instance, v);
			prop.setValue(instance, v);
		} else {
			super.setVariable(name, value, context);
		}
	}

	@Override
	public void execute(ExecutionContext context) {
		Log.m("Executing declaration %s", identifier);

		Object c = coalesce != null ? coalesce.get(context) : null;
		if (c == null) {
			instance = context.newInstance(identifier);
			Log.m("created instance %s", instance);

			block.executeChildren(this, context);
		}
		else {
			instance = c;
		}
	}

	@Override
	public void dump(PrintStream w) {
		w.println("declaration " + identifier);
		block.dump(w);
	}
	
	@Override
	public String toString() {
		return "Declaration " + instance;
	}
}
