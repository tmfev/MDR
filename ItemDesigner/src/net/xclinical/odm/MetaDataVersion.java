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


public class MetaDataVersion {

	private ElementCollection<ItemDef> items = new ElementCollection<ItemDef>();
	
	private ElementCollection<CodeList> codeLists = new ElementCollection<CodeList>();
		
	public void add(ItemDef item) {
		items.add(item);
	}
	
	public ElementCollection<ItemDef> getItems() {
		return items;
	}
	
	public void add(CodeList codeList) {
		codeLists.add(codeList);
	}
	
	public ElementCollection<CodeList> getCodeLists() {
		return codeLists;
	}
}
