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

import java.util.Collection;

/**
 * Handles windows on the screen.
 * 
 * @author ms@xclinical.com
 */
public class WindowManager {

	private static WindowManager instance;
	
	private WindowPresenter canvas;
	
	private WindowPresenter dock;

	private WindowManager() {
	}
	
	public static WindowManager get() {
		if (instance == null) {
			instance = new WindowManager();
		}
		return instance;
	}
	
	public Window getActiveWindow() {
		return canvas.getActiveWindow();
	}
	
	public void setCanvas(WindowPresenter canvas) {
		this.canvas = canvas;
	}
	
	public void setDock(WindowPresenter dock) {
		this.dock = dock;
	}
	
	public Window newWindow() {
		Window w = new Window(this);		
		return w;
	}	

	public Collection<Window> findWindows(WindowState state) {
		switch(state) {
		case MINIMIZED:
			return dock.allWindows();
		case NORMAL:
			return canvas.allWindows();
		default:
			throw new IllegalArgumentException();
		}		
	}
	
	public Clipboard createClipboard() {
		return new Clipboard(this);
	}
		
	void onWindowStateChanged(Window wnd, WindowState newState) {
		switch(newState) {
		case MINIMIZED:
			wnd.changePresenter(dock);
			break;
		case MAXIMIZED:
			canvas.retain(wnd, dock);
			break;
		case NORMAL:
			wnd.changePresenter(canvas);
			break;
		}
	}
	
	void onClose(Window wnd) {
		wnd.changePresenter(null);
	}

	void onActivate(Window wnd) {
		wnd.activateWindow();
	}

	void openWindow(Window wnd) {
		wnd.setState(WindowState.NORMAL);
	}	
}
