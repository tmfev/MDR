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

public class ItemDef implements HasOID {

	private OID oid;

	private String name;
	
	private List<MeasurementUnitRef> measurementUnits = new ArrayList<MeasurementUnitRef>();
	
	private CodeListRef codeList;

	private DataType dataType;

	private Integer length;
	
	private Integer significantDigits;
	
	private TranslatedTextCollection descriptions = new TranslatedTextCollection();

	private TranslatedTextCollection questions = new TranslatedTextCollection();
	
	public OID getOid() {
		return oid;
	}
	
	public void setOid(OID oid) {
		this.oid = oid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public TranslatedTextCollection getDescriptions() {
		return descriptions;
	}
	
	public TranslatedTextCollection getQuestions() {
		return questions;
	}
	
	public void add(MeasurementUnitRef measurementUnitRef) {
		measurementUnits.add(measurementUnitRef);
	}
	
	public Collection<MeasurementUnitRef> getMeasurementUnits() {
		return measurementUnits;
	}
	
	public CodeListRef getCodeList() {
		return codeList;
	}
	
	public void setCodeList(CodeListRef codeList) {
		this.codeList = codeList;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	public Integer getLength() {
		return length;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public Integer getSignificantDigits() {
		return significantDigits;
	}
	
	public void setSignificantDigits(Integer significantDigits) {
		this.significantDigits = significantDigits;
	}
	
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
