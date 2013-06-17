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
package com.xclinical.mdr.client.ui;

import com.google.gwt.core.client.GWT;

/**
 * The root of the object hierarchy on the client.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: 242 $
 */
public class Platform {

	private static Workbench workbench;
	
	/**
	 * Defines constant values used throughout the user interface.
	 */
	public static final UIConstants CONSTANTS = GWT.create(UIConstants.class);
	
	/**
	 * Initialized the platform.
	 * 
	 * This method must be called exactly once at application startup.
	 * 
	 * It is called by the framework and not intended to be called by client code.
	 */
	public static void initialize(String name) {
		if (workbench != null) throw new IllegalStateException("already initialized");
		workbench = new Workbench(name);
	}
	
	/**
	 * Retrieves the current workbench.
	 * @return the current workbench.
	 */
	public static Workbench getWorkbench() {
		if (workbench == null) throw new IllegalStateException("The workbench has not been initialized, yet");
		return workbench;
	}

}
