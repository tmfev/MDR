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

public class Import implements Statement {

	private final String name;
	
	private final Class<?> clazz;
	
	public Import(String name) {
		this.name = name;
		try {
			this.clazz = Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public void execute(ExecutionContext context) {
		context.addVariable(clazz.getSimpleName(), new ObjectReference(clazz));
	}
	
	public Class<?> resolve(String name) {
		if (clazz.getSimpleName().equals(name)) {
			return clazz;
		}
		else {
			return null;
		}
	}
	
	@Override
	public void dump(PrintStream w) {
		w.println("Import " + name);
	}
}
