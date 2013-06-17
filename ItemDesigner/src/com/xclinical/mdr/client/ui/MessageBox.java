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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.xclinical.mdr.client.ui.MessagePanel.Button;

/**
 * A generic dialog to show simple text messages.
 * 
 * Remember to avoid being modal. Avoid using this class if you can.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
public class MessageBox extends PopupDialog<MessagePanel> {

	/**
	 * Constructs a new message box and sets the specified text.
	 * @param text is the text of the message box.
	 */
	public MessageBox(String text) {
		this();
		setText(text);
	}

	/**
	 * Constructs a new message box and sets the specified text and comment.
	 * @param text is the text of the message box.
	 * @param comment is the comment of the message box.
	 */
	public MessageBox(String text, String comment) {
		this();
		setText(text);
		setComment(comment);
	}
	
	/**
	 * Constructs a new message box.
	 */
	public MessageBox() {
		super(new MessagePanel(), true);

	}
	
	/**
	 * Specifies if the spin wait icon is visible.
	 * @param visible <code>true</code> iff the icon is visible.
	 */
	public final void spinWait(boolean visible) {
		getBody().spinWait(visible);
	}
	
	/**
	 * Shows the specified image.
	 * @param image is the image to show.
	 */
	public void setImage(Image image) {
		getBody().setImage(image);
	}
	
	/**
	 * Sets the text of the message.
	 * @param text is the text.
	 */
	public final void setText(String text) {
		getBody().setText(text);
	}

	/**
	 * Sets the comment string.
	 * @param comment is the comment string.
	 */
	public final void setComment(String comment) {
		setComment(comment, false);
	}

	/**
	 * Sets the comment string.
	 * @param comment is the comment.
	 * @param isHTML <code>true</code> iff the comment string is formatted as HTML.
	 */
	public final void setComment(String comment, boolean isHTML) {
		getBody().setComment(comment, isHTML);
	}

	/**
	 * Adds a new button to the message box.
	 * 
	 * @return the new button.
	 */
	public final Button addButton(boolean autoHide) {
		Button button = getBody().addButton();
		if (autoHide) {
			button.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					MessageBox.this.hide();
				}
			});
		}
		
		return button;
	}

	public final Button addButton(String text, Command command, boolean autoHide) {
		Button button = addButton(autoHide);
		button.setText(text);
		button.setCommand(command);
		return button;
	}
	
	/**
	 * Adds a new button with a specified text and command.
	 * 
	 * @param text is the text of the button.
	 * @param command is the command to perform when the button is pressed.
	 * @return the new button.
	 */
	public final Button addButton(String text, Command command) {
		return addButton(text, command, true);
	}
	
	/**
	 * Shows the message box to the user.
	 */
	public final void showPopup() {
		Platform.getWorkbench().showPopup(this);
	}
}
