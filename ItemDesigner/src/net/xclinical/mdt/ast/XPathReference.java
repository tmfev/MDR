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

import javax.xml.xpath.XPathExpressionException;

import net.xclinical.mdt.ProcessingException;

public class XPathReference implements Reference {

	private final Reference reference;
	
	public XPathReference(Reference reference) {
		this.reference = reference;
	}
	
	@Override
	public Object get(ExecutionContext context) {
		try {
			String xp = String.valueOf(reference.get(context));			
			return new XPathResult(context.getNode(), xp); 
		}
		catch(XPathExpressionException e) {
			throw new ProcessingException(e);
		}
	}
	
	@Override
	public String toString() {
		return "xpath " + String.valueOf(reference);
	}
}
