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

public class ODM130 {

	public static final String NAMESPACE = "http://www.cdisc.org/ns/odm/v1.3";
	
	public static final class ODM {
		public static final String ELEMENT = "ODM";
	}
	
	public static final class Study {	
		public static final String ELEMENT = "Study";
	}

	public static final class BasicDefinitions {	
		public static final String ELEMENT = "BasicDefinitions";
	}
	
	public static final class MetaDataVersion {
		public static final String ELEMENT = "MetaDataVersion";		
	}

	public static final class MeasurementUnit {	
		public static final String ELEMENT = "MeasurementUnit";
		
		public static final String OID = "OID";
		public static final String NAME = "Name";		
	}

	public static final class MeasurementUnitRef {	
		public static final String ELEMENT = "MeasurementUnitRef";
		
		public static final String MEASUREMENT_UNIT_OID = "MeasurementUnitOID";		
	}
	
	public static final class ItemDef {
		public static final String ELEMENT = "ItemDef";
		
		public static final String OID = "OID";
		public static final String DATA_TYPE = "DataType";		
	}

	public static final class CodeList {
		public static final String ELEMENT = "CodeList";
		
		public static final String OID = "OID";
		public static final String DATA_TYPE = "DataType";		
	}

	public static final class CodeListRef {
		public static final String ELEMENT = "CodeListRef";
		
		public static final String CODE_LIST_OID = "CodeListOID";
	}
	
	public static final class CodeListItem {
		public static final String ELEMENT = "CodeListItem";
		
		public static final String CODEDVALUE = "CodedValue";
		public static final String RANK = "Rank";		
		public static final String DECODE = "Decode";		
	}
	
	public static final class TranslatedText {
		public static final String ELEMENT = "TranslatedText";		
	}
}
