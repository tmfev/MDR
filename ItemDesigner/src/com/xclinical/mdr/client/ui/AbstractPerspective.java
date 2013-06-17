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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * A reusable implementation of {@link Perspective}.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: $
 */
public abstract class AbstractPerspective implements Perspective {

	public void detach() {
	}

	public void showPopup(final PopupPanel panel) {
		panel.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				panel.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, 0);
			}
		});
	}
	
	public void openView(String id) {
		throw new UnsupportedOperationException();
	}	
}
