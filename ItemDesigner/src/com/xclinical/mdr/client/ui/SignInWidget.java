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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.client.util.MessageFormat;
import com.xclinical.mdr.client.util.Styles;

public class SignInWidget extends Composite {

	interface Binder extends UiBinder<Widget, SignInWidget> {}

	@UiField
	DivElement message;

	@UiField
	TextBox email;

	@UiField
	PasswordTextBox password;

	@UiField
	CheckBox stayLoggedIn;

	@UiField
	SpanElement notRecommended;

	@UiField
	PushButton login;
	
	public SignInWidget() {
		this(MessageFormat.format(Platform.CONSTANTS.signInMessage(), Platform.getWorkbench().getName()));
	}
	
	public SignInWidget(String message) {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));

		this.message.setInnerText(message);
		
		HorizontalPanel buttons = new HorizontalPanel();
		Styles.setFloat(buttons, Styles.FLOAT_RIGHT);
		
		PushButton login = new PushButton("Login", new ClickHandler(){
			public void onClick(ClickEvent arg0) {
				login();
			}});
		login.setAccessKey('l');
		buttons.add(login);
		
		email.setFocus(true);
	}

	@UiHandler("stayLoggedIn")
	void stayLoggedClick(ClickEvent e) {
		notRecommended.setInnerText(Platform.CONSTANTS.stayLoggedInNotRecommended());
	}
	
	@UiHandler("login")
	void loginClick(ClickEvent e) {
		login();		
	}

	@UiHandler({"email", "password"})
	void checkEnter(KeyDownEvent e) {
		if (e.getNativeKeyCode() == '\r') {
			login();
		}
	}
	
	private void login() {
		LoginUtils.login(email.getText(), password.getText(), stayLoggedIn.getValue());
	}
}
