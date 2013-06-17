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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.xclinical.mdr.server.util.Strings;

import net.xclinical.mdt.Log;
import net.xclinical.mdt.ProcessingException;

public class Function implements Reference, Statement {

	private List<Reference> params = new ArrayList<Reference>();
	
	private String spec;
	
	public Function() {
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
	
	public void addParameter(Reference param) {
		params.add(param);
	}

	private Object invoke(String funcName, Class<?> type, Object instance, ExecutionContext context) {
		Method[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (m.getName().equals(funcName) && m.getParameterTypes().length == params.size()) {
				Object[] p = new Object[params.size()];
				for (int j = 0; j < p.length; j++) {
					Class<?> parameterType = m.getParameterTypes()[j];
					Object parameter = params.get(j).get(context);
					if (parameter == null || parameterType.isInstance(parameter)) {
						p[j] = parameter;
					}
					else if (parameterType.isAssignableFrom(String.class)) {
						p[j] = String.valueOf(parameter);				
					}
					else {
						throw new ProcessingException("Invocation of " + funcName + " on " + instance + " failed");
					}
				}
				try {
					Log.m("Invocation parameters are %s", Strings.implode(",", p));
					return m.invoke(instance, p);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException("Invocation of " + funcName + " on " + instance + " failed", e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException("Invocation of " + funcName + " on " + instance + " failed", e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException("Invocation of " + funcName + " on " + instance + " failed", e);
				}
			}
		}
		
		throw new IllegalArgumentException("No matching method " + funcName + " found in type " + type);		
	}
	
	@Override
	public Object get(ExecutionContext context) {
		Log.m("Evaluating %s", this);
		String[] parts = spec.split("\\.");
		
		Object instance;
		String funcName;
		
		if (parts.length == 1) {
			funcName = parts[0];		
			instance = context.getVariable(Scope.THIS, Object.class);					
		}
		else {
			String var = parts[0];
			funcName = parts[1];		
			instance = context.getVariable(var, Object.class);			
		}
		
		final Object result;
		
		if (instance instanceof Class) {
			result = invoke(funcName, (Class<?>)instance, null, context);
		}
		else {
			if (instance == null) {
				throw new NullPointerException("Invocation of " + this + " on null reference failed");
			}
			Class<?> type = instance.getClass();
			result = invoke(funcName, type, instance, context);			
		}
		
		Log.m("Evaluation result is %s", result);
		return result;		
	}

	@Override
	public void dump(PrintStream w) {
		w.println(toString());		
	}

	@Override
	public void execute(ExecutionContext context) {
		Log.m("Executing %s", this);
		Object obj = get(context);
		Log.m("Result is %s (%s)", obj, obj == null ? "" : obj.getClass());
	}
	
	@Override
	public String toString() {
		return "func " + spec + "(" + params.size() + ")";
	}	
}
