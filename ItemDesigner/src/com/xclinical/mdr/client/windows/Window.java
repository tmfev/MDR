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
package com.xclinical.mdr.client.windows;

import com.google.gwt.user.client.ui.IsWidget;

public class Window {

	private WindowState state = WindowState.UNDEFINED;
	
	private final WindowManager manager;
	
	private WindowPresenter presenter;

	private String title;
	
	private IsWidget body;
	
	Window(WindowManager manager) {
		this.manager = manager;		
	}
	
	public void setBody(IsWidget body) {
		this.body = body;
		if (presenter != null) {
			presenter.onBodyChanged(this);
		}
		
		if (body instanceof HasWindow) {
			((HasWindow)body).setWindow(this);
		}
	}
	
	public IsWidget getBody() {
		return body;
	}
	
	public void setTitle(String title) {
		this.title = title;
		if (presenter != null) {
			presenter.onTitleChanged(this);
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	WindowState getState() {
		return state;
	}
	
	void setState(WindowState state) {
		if (this.state != state) {
			manager.onWindowStateChanged(this, state);
			this.state = state;
		}
	}

	public void close() {		
		manager.onClose(this);
	}

	public void activate() {
		manager.onActivate(this);
	}

	public boolean isActive() {
		if (presenter != null) {
			return presenter.isWindowActive(this);
		}
		else {
			return false;
		}
	}
	
	public void open() {
		manager.openWindow(this);
	}
	
	void changePresenter(WindowPresenter presenter) {
		if (this.presenter != null) {
			this.presenter.removeWindow(this);
		}

		this.presenter = presenter;
		
		if (presenter != null) {
			presenter.addWindow(this);
		}
	}
		
	void activateWindow() {
		if (presenter != null) {		
			presenter.setActive(this);
		}
	}

}
