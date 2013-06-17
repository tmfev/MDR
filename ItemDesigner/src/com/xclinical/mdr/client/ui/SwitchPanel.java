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

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class SwitchPanel extends Composite implements HasWidgets {

	private FlowPanel body;

	public SwitchPanel() {
		initWidget(body = new FlowPanel());
	}
	
	public void setVisibleWidget(int index) {
		for (int i = 0; i < body.getWidgetCount(); i++) {
			body.getWidget(i).setVisible(i == index);
		}
	}

	public void setVisibleWidget(Widget v) {
		for (int i = 0; i < body.getWidgetCount(); i++) {
			Widget w = body.getWidget(i);
			w.setVisible(w == v);
		}
	}
	
	@Override
	public void add(Widget widget) {
		widget.setVisible(false);
		body.add(widget);
	}

	@Override
	public void clear() {
		body.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return body.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return body.remove(w);
	}
}
