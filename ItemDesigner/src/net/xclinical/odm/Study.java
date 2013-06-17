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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Study {

	private OID oid;
	
	private GlobalVariables globalVariables;

	private BasicDefinitions basicDefinitions;
	
	private List<MetaDataVersion> metaDataVersions = new ArrayList<MetaDataVersion>();
	
	public Study() {
		globalVariables = new GlobalVariables();
		basicDefinitions = new BasicDefinitions();
	}
	
	public OID getOid() {
		return oid;
	}
	
	public void setOid(OID oid) {
		this.oid = oid;
	}
	
	public GlobalVariables getGlobalVariables() {
		return globalVariables;
	}
	
	public void setGlobalVariables(GlobalVariables globalVariables) {
		this.globalVariables = globalVariables;
	}
	
	public BasicDefinitions getBasicDefinitions() {
		return basicDefinitions;
	}
	
	public void setBasicDefinitions(BasicDefinitions basicDefinitions) {
		this.basicDefinitions = basicDefinitions;
	}
	
	public void add(MetaDataVersion metaDataVersion) {
		metaDataVersions.add(metaDataVersion);
	}
	
	public Collection<MetaDataVersion> getMetaDataVersions() {
		return metaDataVersions;
	}
}
