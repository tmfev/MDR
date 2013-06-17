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

public class Conditional extends Scope implements Reference, Statement {

	public Reference condition;

	public Block block = new Block();
	
	public Conditional() {
	}

	public Block getBlock() {
		return block;
	}
	
	public void setCondition(Reference condition) {
		this.condition = condition;
	}
	
	public Reference getCondition() {
		return condition;
	}

	@Override
	public Object get(ExecutionContext context) {
		return null;
	}

	@Override
	public void execute(ExecutionContext context) {
		Log.m("Executing condition %s", condition);

		Object c = condition.get(context);
		if (c.equals(Boolean.TRUE)) {
			block.executeChildren(this, context);			
		}
		else {
			Log.m("Result is false");
		}
	}

	@Override
	public void dump(PrintStream w) {
		w.println("condition " + condition);
		block.dump(w);
	}
	
	@Override
	public String toString() {
		return "Condition " + condition;
	}
}
