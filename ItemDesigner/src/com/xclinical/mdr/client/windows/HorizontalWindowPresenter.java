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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HorizontalWindowPresenter extends Composite implements WindowPresenter {

	interface Binder extends UiBinder<Widget, HorizontalWindowPresenter> {}
	
	@UiField
	HorizontalPanel panel;
	
	private Map<Window, FrameWindowPresentation> windows = new HashMap<Window, FrameWindowPresentation>();
	
	public HorizontalWindowPresenter() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
	}

	@Override
	public void addWindow(final Window wnd) {		
		FrameWindowPresentation frame = new FrameWindowPresentation() {
			@Override
			protected void onCloseClicked(ClickEvent event) {
				wnd.close();
			}

			@Override
			protected void onMinimizeClicked(ClickEvent event) {
				wnd.setState(WindowState.MINIMIZED);
			}
			
			@Override
			protected void onMaximizeClicked(ClickEvent event) {
				wnd.setState(WindowState.MAXIMIZED);
			}
			
			@Override
			public void onClick(ClickEvent event) {
				wnd.activate();
			}
		};
		windows.put(wnd, frame);
		panel.add(frame);
		panel.setCellHeight(frame, "100%");
		frame.setBody(wnd.getBody());
		frame.setText(wnd.getTitle());
	}
	
	@Override
	public void removeWindow(Window wnd) {
		FrameWindowPresentation frame = windows.get(wnd);
		panel.remove(frame);
		windows.remove(wnd);
	}
	
	@Override
	public void onBodyChanged(Window wnd) {
		FrameWindowPresentation frame = windows.get(wnd);
		frame.setBody(wnd.getBody());
	}

	@Override
	public void onTitleChanged(Window wnd) {
		FrameWindowPresentation frame = windows.get(wnd);
		frame.setText(wnd.getTitle());		
	}
	
	@Override
	public void setActive(Window wnd) {
		for (Map.Entry<Window, FrameWindowPresentation> entry : windows.entrySet()) {
			entry.getValue().setActive(entry.getKey() == wnd);
		}
	}
	
	@Override
	public Window getActiveWindow() {
		for (Map.Entry<Window, FrameWindowPresentation> entry : windows.entrySet()) {
			if (entry.getValue().isActive()) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	@Override
	public boolean isWindowActive(Window wnd) {
		return windows.get(wnd).isActive();
	}
	
	@Override
	public void retain(Window wnd, WindowPresenter target) {
		for (Window w : windows.keySet()) {
			if (w != wnd) {
				w.changePresenter(target);
			}
		}
	}
	
	@Override
	public Collection<Window> allWindows() {
		return windows.keySet();
	}
}
