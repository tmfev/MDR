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

public class CodeListItem {

	private String codedValue;
	
	private int rank;
	
	private TranslatedTextCollection decode = new TranslatedTextCollection();
	
	public String getCodedValue() {
		return codedValue;
	}
	
	public void setCodedValue(String codedValue) {
		this.codedValue = codedValue;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public TranslatedTextCollection getDecode() {
		return decode;
	}
	
	public void setDecode(TranslatedTextCollection decode) {
		this.decode = decode;
	}
}
