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
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.xclinical.iso11179.ext.Visitable;
import net.xclinical.iso11179.ext.Visitor;

import com.xclinical.mdr.server.PMF;

/**
 * Represents an Designation_Context association class (7.3.3.2).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "DESIGNATION_CONTEXT")
@IdClass(DesignationContextId.class)
public class DesignationContext implements Visitable {

	@Id
	@ManyToOne(targetEntity=Designation.class)
	Designation designation;

	@Id
	@ManyToOne(targetEntity=Context.class)
	Context context;

	public DesignationContext() {
	}

	public DesignationContext(Designation designation, Context context) {
		if (designation == null) throw new NullPointerException("Cannot create a designation context without designation");
		if (context == null) throw new NullPointerException("Cannot create a designation context without a context");
		
		this.designation = designation;
		this.context = context;

		designation.addScope(this);
		context.addRelevantDesignation(this);
	}

	public Context getContext() {
		return context;
	}
	
	public Designation getDesignation() {
		return designation;		
	}
	
	public static DesignationContext create(Designation designation, Context context) {
		DesignationContextId id = new DesignationContextId(designation, context);
		DesignationContext dc = PMF.get().find(DesignationContext.class, id);
		if (dc == null) {
			dc = new DesignationContext(designation, context);
			PMF.get().persist(dc);
		}
		
		return dc;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
