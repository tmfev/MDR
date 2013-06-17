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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.client.util.MessageFormat;

public class SignUpWidget extends Composite {

	interface MyUiBindder extends UiBinder<Widget, SignUpWidget> {}
	private static MyUiBindder uiBinder = GWT.create(MyUiBindder.class);
	
	@UiField
	HTML message;

	@UiField
	PushButton signUp;
	
	public SignUpWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		message.setText(MessageFormat.format(Platform.CONSTANTS.signUpMessage(), Platform.getWorkbench().getName()));
	}

	@UiHandler("signUp")
	void loginClick(ClickEvent e) {
		LoginUtils.register(null, null);
	}	
}
