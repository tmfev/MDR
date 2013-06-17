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
package net.xclinical.odm;

public abstract class Ref {

	private OID targetOid;
	
	private int orderNumber = -1;
	
	private boolean mandatory;
	
	private OID collectionExceptionConditionOid;
	
	public OID getTargetOid() {
		return targetOid;
	}
	
	public void setTargetOid(OID targetOid) {
		this.targetOid = targetOid;
	}
	
	public int getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public OID getCollectionExceptionConditionOid() {
		return collectionExceptionConditionOid;
	}
	
	public void setCollectionExceptionConditionOid(OID collectionExceptionConditionOid) {
		this.collectionExceptionConditionOid = collectionExceptionConditionOid;
	}
	
}
