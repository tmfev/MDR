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
package com.xclinical.mdr.client.util;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Image;
import com.xclinical.mdr.client.ItemDesigner;
import com.xclinical.mdr.client.SessionChangedEvent;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.ErrorResponse;
import com.xclinical.mdr.client.io.OnErrorHandler;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.LoginCommand;
import com.xclinical.mdr.client.iso11179.model.LoginInfo;
import com.xclinical.mdr.client.iso11179.model.RefreshSessionCommand;
import com.xclinical.mdr.client.silk.SilkImageBundle;
import com.xclinical.mdr.client.ui.MessageBox;
import com.xclinical.mdr.client.ui.Platform;

/**
 * Provides access to login information.
 * 
 * @author michael@mictale.com
 * @version $Revision: 267 $
 *
 */
public final class LoginUtils {

	private static final long OFFSET = 1000 * 60 * 60  * 24 * 7;
	
	private static final String NAME = "net.xclinical.sid";
	
	private static LoginInfo currentLoginInfo;
		
	private LoginUtils() {
	}
	
	public static LoginInfo getCurrentLoginInfo() {
		return currentLoginInfo;
	}
	
	/**
	 * Changes the current session.
	 * @param session is the new session info.
	 */
	private static void changeSession(LoginInfo session) {
		currentLoginInfo = session;
		ItemDesigner.get().getEventBus().fireEvent(new SessionChangedEvent(session));
	}

	/**
	 * Clears the current user session.
	 */
	public static void clearSession() {
		changeSession(null);
	}
	
	/**
	 * Checks if a session is currently existing.
	 * @return {@code true} if a session is existing.
	 */
	public static boolean hasSession() {
		return currentLoginInfo != null;
	}
	
	/**
	 * Retrieves the current session.
	 * @return the current session.
	 */
	public static String currentSession() {
		return currentLoginInfo == null ? null : currentLoginInfo.getSession();
	}
	
	/**
	 * Logs out the current user.
	 */
	public static void logout() {
		LoginUtils.clearCredentials();
		changeSession(null);
		// TODO: Show login dialog.
	}
	
	/**
	 * Logs in using the specified credentials.
	 * @param save 
	 * 
	 * @param credentials contains the login credentials.
	 */
	public static void login(String name, String password, final boolean save) {
		final MessageBox messageBox = new MessageBox(Platform.CONSTANTS.authenticatingText(), 
				Platform.CONSTANTS.authenticatingDetails());
		messageBox.spinWait(true);
		messageBox.showPopup();
		
		LoginCommand command = LoginCommand.newInstance(name, password);
		CommandExecutor<LoginInfo> ex = new CommandExecutor<LoginInfo>();
		ex.setOnError(new OnErrorHandler() {
			@Override
			public void onError(ErrorResponse reason) {
				messageBox.setImage(new Image(SilkImageBundle.INSTANCE.error()));
				messageBox.setText(reason.getMessage());
				messageBox.setComment(reason.getDetails());
				messageBox.addButton(Platform.CONSTANTS.dialogClose(), null);
				messageBox.showPopup();
				LoginUtils.clearCredentials();
				changeSession(null);
			}
		});
		ex.setOnResult(new OnSuccessHandler<LoginInfo>() {
			@Override
			public void onSuccess(LoginInfo obj) {
				messageBox.hide();

				if (save) {
					storeCredentials(obj.getSession());
				}
				
				changeSession(obj);
			}
		});
		ex.invoke(LoginCommand.PATH, command);		
	}

	public static void refresh(String session) {
		final MessageBox messageBox = new MessageBox(Platform.CONSTANTS.authenticatingText(), 
				Platform.CONSTANTS.authenticatingDetails());
		messageBox.spinWait(true);
		messageBox.showPopup();
		
		RefreshSessionCommand command = RefreshSessionCommand.newInstance(session);
		CommandExecutor<LoginInfo> executor = new CommandExecutor<LoginInfo>();
		executor.setOnError(new OnErrorHandler() {
			@Override
			public void onError(ErrorResponse reason) {
				messageBox.setImage(new Image(SilkImageBundle.INSTANCE.error()));
				messageBox.setText(reason.getMessage());
				messageBox.setComment(reason.getDetails());
				messageBox.addButton(Platform.CONSTANTS.dialogClose(), null);
				messageBox.showPopup();
				LoginUtils.clearCredentials();
				changeSession(null);
			}
		});
		executor.setOnResult(new OnSuccessHandler<LoginInfo>() {
			@Override
			public void onSuccess(LoginInfo obj) {
				messageBox.hide();
				changeSession(obj);
			}
		});
		executor.invoke(RefreshSessionCommand.PATH, command);
	}
	
	/**
	 * Registers a new user.
	 * 
	 * @param credentials are the login credentials.
	 */
	public static void register(String name, String password) {
	}	
	
	/**
	 * Stores the specified credentials.
	 * 
	 * @param credentials are the credentials to save.
	 */
	public static void storeCredentials(String cookie) {
		Date expiration = new Date(new Date().getTime() + OFFSET);
		Cookies.setCookie(NAME, cookie, expiration);
	}

	
	/**
	 * Retrieves the current credentials.
	 * @return the current credentials.
	 */
	public static String getCredentials() {
		String sid = Cookies.getCookie(NAME);
		return sid;
	}
	
	/**
	 * Clears the credentials store.
	 */
	public static void clearCredentials() {
		Cookies.removeCookie(NAME);
	}
}
