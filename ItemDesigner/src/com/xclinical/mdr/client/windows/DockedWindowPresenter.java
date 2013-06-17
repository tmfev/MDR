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
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DockedWindowPresenter extends Composite implements WindowPresenter {

	HorizontalPanel panel;
	
	private Map<Window, DockedWindowPresentation> icons = new HashMap<Window, DockedWindowPresentation>();
	
	public DockedWindowPresenter() {
		FlowPanel p = new FlowPanel();
		p.add(panel = new HorizontalPanel());
		initWidget(p);
	}

	@Override
	public void addWindow(final Window wnd) {
		DockedWindowPresentation icon = new DockedWindowPresentation() {
			@Override
			protected void onCloseClicked(ClickEvent event) {
				wnd.close();				
			}
			
			@Override
			protected void onMaximizeClicked(ClickEvent event) {
				wnd.setState(WindowState.MAXIMIZED);
			}

			@Override
			public void onClick(ClickEvent event) {
				wnd.setState(WindowState.NORMAL);
				wnd.activate();
			}
		};
		
		icons.put(wnd, icon);		
		panel.add(icon);
		icon.setText(wnd.getTitle());
	}

	@Override
	public void removeWindow(Window wnd) {
		DockedWindowPresentation icon = icons.get(wnd);
		panel.remove(icon);
		icons.remove(wnd);
	}
	
	@Override
	public void onBodyChanged(Window window) {		
	}
	
	@Override
	public void onTitleChanged(Window wnd) {
		DockedWindowPresentation icon = icons.get(wnd);
		icon.setTitle(wnd.getTitle());
	}
	
	@Override
	public void setActive(Window window) {
	}
	
	@Override
	public Window getActiveWindow() {
		return null;
	}
	
	@Override
	public boolean isWindowActive(Window window) {
		return false;
	}
	
	@Override
	public void retain(Window wnd, WindowPresenter dock) {
	}
	
	@Override
	public Collection<Window> allWindows() {
		return icons.keySet();
	}
}
