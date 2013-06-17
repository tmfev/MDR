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
package net.xclinical.iso11179;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.server.PMF;

/**
 * Represents an Definition_Context association class (7.3.3.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "DEFINITION_CONTEXT")
public class DefinitionContext implements Visitable {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Definition definition;

	@ManyToOne
	private Context context;

	public DefinitionContext() {
	}

	public DefinitionContext(Definition definition, Context context) {		
		this.definition = definition;
		this.context = context;
		
		definition.addScope(this);
		context.addRelevantDefinition(this);
	}
	
	public Long getId() {
		return id;
	}
	
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}
	
	public Definition getDefinition() {
		return definition;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
	
	public static DefinitionContext create(Definition definition, Context context) {
		DefinitionContext ctx = new DefinitionContext(definition, context);
		PMF.get().persist(ctx);
		return ctx;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
