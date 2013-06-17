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

import com.google.gwt.i18n.client.Constants;

/**
 * Defines constants used throughout the user interface.
 * 
 * Clients usually dereference {@link Platform#CONSTANTS} to access the values.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
public interface UIConstants extends Constants {

	/**
	 * A message that shows up on the sign-in dialog.
	 * @return the sign-in message.
	 */
	String signInMessage();
	
	/**
	 * A warning message that shows if the user selected to stay logged in.
	 * @return the warning message.
	 */
	String stayLoggedInNotRecommended();
	
	/**
	 * A checkbox that the user selects to stay logged in on this machine.
	 * @return the message.
	 */
	String rememberMe();
	
	/**
	 * The text of a dialog that allows to sign up.
	 * @return the message.
	 */
	String signUpMessage();

	String authenticatingText();
	
	String authenticatingDetails();
	
	/**
	 * Generic dialog close button.
	 * @return the button text.
	 */
	String dialogClose();
	
	/**
	 * Generic dialog cancel button.
	 * @return the button text.
	 */
	String dialogCancel();
	
	/**
	 * Command to clear a list.
	 * @return the command text.
	 */
	String clearList();
	
	/**
	 * A message to ask the user to save something.
	 * @return the message.
	 */
	String askSaveText();

	/**
	 * A descriptive text to ask the user to save an entity.
	 * @return the message.
	 */
	String askSaveDescription();
	
	String askSaveDont();
	
	String askSaveSave();
	
	String exceptionText();
	
	String exceptionLessInfo();
	
	String exceptionMoreInfo();

	String sessionExpiredMessage();
		
	String me();
	
	String sliderYes();
	
	String sliderNo();
	
}
