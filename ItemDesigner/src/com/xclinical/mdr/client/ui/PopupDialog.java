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

import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple popup panel that contains a single widgets as its child.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 * @param <T> is the type of the child window.
 */
public class PopupDialog<T extends Widget> extends DecoratedPopupPanel {

	private T body;

	public PopupDialog() {
		super();
	}
	
	public PopupDialog(T body) {
		this(body, false);
	}
	
	public PopupDialog(T body, boolean autoHide) {
		super(autoHide);
		
		this.body = body;
		add(body);
	}
	
	public T getBody() {
		return body;
	}
	
	public void setBody(T body) {
		clear();
		add(body);
	}
}
