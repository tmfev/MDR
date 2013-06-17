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

import java.io.Serializable;

public class DesignationContextId implements Serializable {

	private static final long serialVersionUID = 5791070934836538287L;

	private Designation designation;

	private Context context;

	public DesignationContextId() {
	}

	public DesignationContextId(Designation designation, Context context) {
		this.designation = designation;
		this.context = context;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DesignationContextId) {
			DesignationContextId other = (DesignationContextId)obj;
			return other.context.getId().equals(context.getId()) && other.designation.getId().equals(designation.getId());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (int)(designation.getId() ^ context.getId());
	}
}
