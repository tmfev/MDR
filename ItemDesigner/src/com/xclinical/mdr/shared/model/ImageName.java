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
package com.xclinical.mdr.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum ImageName implements IsSerializable {

	CALENDAR("calendar"),
	CHART_GANTT("chart_gantt"),
	CHART_AREA("chart_area"),
	CUBE_GREY("cube_grey"),
	CURRENCY_EURO("currency_euro"),
	DESKTOP("desktop"),
	DOCUMENT("document"),
	ERROR("error"),
	FACTORY("factory"),
	FIND("find"),
	FIT_TO_SIZE("fit_to_size"),
	FLAG_GENERIC("flag_generic"),
	GARBAGE("garbage"),
	GEAR("gear"),
	GEARS("gears"),
	HELP2("help2"),
	HOUSE("house"),
	REFRESH("refresh"),
	SHELF("shelf"),
	SYMBOL_QUESTIONMARK("symbol_questionmark"),
	USER("user"),
	MASKS("masks"),
	MAGIC_WAND("magic_wand"),

	EXTEND_COLD("extendCold"),
	EXTEND_HOT("extendHot");

	private String name;
	
	ImageName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
