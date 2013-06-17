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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

abstract class FrameWindowPresentation extends Composite implements ClickHandler {

	interface Binder extends UiBinder<Widget, FrameWindowPresentation> {}
	
	interface Style extends CssResource {
		String headerActive();

		String panelActive();
	}
	
	@UiField
	protected Style style;
	
	@UiField
	protected Label title;

	@UiField
	protected SimplePanel bodyContainer;

	@UiField
	protected FlowPanel panel;

	@UiField
	protected FlowPanel header;
	
	@UiField
	protected Image close;

	@UiField
	protected Image minimize;
	
	@UiField
	protected Image maximize;

	private boolean active;
	
	FrameWindowPresentation() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		panel.addDomHandler(this, ClickEvent.getType());
	}

	public void showClose(boolean show) {
		close.setVisible(show);
	}
	
	public void setText(String text) {
		title.setText(text);
	}

	public void setActive(boolean b) {
		if (b != active) {
			if (b) {
				header.addStyleName(style.headerActive());
				panel.addStyleName(style.panelActive());
			}
			else {
				header.removeStyleName(style.headerActive());
				panel.removeStyleName(style.panelActive());
			}
			active = b;
		}
	}

	public boolean isActive() {
		return active;
	}
	
	void setBody(IsWidget body) {
		bodyContainer.setWidget(body);
	}
	
	@UiHandler("close")
	protected abstract void onCloseClicked(ClickEvent event);

	@UiHandler("minimize")
	protected abstract void onMinimizeClicked(ClickEvent event);

	@UiHandler("maximize")
	protected abstract void onMaximizeClicked(ClickEvent event);
	
	@Override
	public abstract void onClick(ClickEvent event);		
}
