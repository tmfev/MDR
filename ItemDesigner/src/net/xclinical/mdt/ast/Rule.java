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

import javax.xml.xpath.XPathExpressionException;

import net.xclinical.mdt.Log;
import net.xclinical.mdt.ProcessingException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Rule extends Scope implements Statement {

	private Reference xpath;

	private Block block = new Block();

	public Rule() {
	}

	public Block getBlock() {
		return block;
	}

	public void setXPath(Reference xpath) {
		this.xpath = xpath;
	}

	@Override
	public void execute(ExecutionContext context) {
		Log.m("Executing rule: " + xpath);

		if (xpath == null) {
			throw new IllegalArgumentException("No xpath for rule");
		}

		XPathResult result = (XPathResult) xpath.get(context);
		try {
			NodeList nodeList = result.asList();

			final Node n = context.getNode();

			Log.m("found %d matches", nodeList.getLength());

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node contextNode = nodeList.item(i);
				context.setNode(contextNode);
				block.executeChildren(this, context);
			}

			context.setNode(n);

		} catch (XPathExpressionException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	public void dump(PrintStream w) {
		w.println(toString());
		block.dump(w);
	}

	@Override
	public String toString() {
		return "Rule " + xpath;
	}

	@Override
	public void setVariable(String name, Reference value, ExecutionContext context) {
		context.writeVariable(name, value);
	}

	@Override
	public <T> T getVariable(String name, Class<T> type, ExecutionContext context) {
		return context.readVariable(name, type);
	}
}
