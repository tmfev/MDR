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

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Performs basic layout.
 * 
 * A client usually implements one or more perspectives and adds instances
 * to a {@link Workbench} with calls to {@link Workbench#addPerspective(String, Perspective)}.
 * Then, the client calls {@link Workbench#activatePerspective(String)} to start using
 * the specified perspective.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: $
 */
public interface Perspective {

	/**
	 * Attaches the current perspective to the specified workbench.
	 *
	 * @param wb is the workbench that activates the current perspective.
	 */
	void attach(Workbench wb);
	
	/**
	 * Called while detaching the current perspective.
	 */
	void detach();
	
	/**
	 * Shows the specified popup panel.
	 * 
	 * @param popup is the panel to show.
	 */
	void showPopup(PopupPanel popup);	
}
