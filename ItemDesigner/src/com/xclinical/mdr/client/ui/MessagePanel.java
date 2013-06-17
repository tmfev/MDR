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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.res.StandardImageBundle;

public class MessagePanel extends Composite {

	private FlexTable body;
	
	private final HorizontalPanel buttons;

	private final Image image;
	
	private final HTML commentLabel;

	/**
	 * Specializes push buttons for message boxes.
	 *
	 */
	public class Button extends PushButton {
		private Command command;
		
		Button() {
			addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					if (command != null) {
						command.execute();
					}
				}
			});
		}
		
		/**
		 * Sets the command of the current button.
		 * 
		 * @param command is the command to perform.
		 */
		public void setCommand(Command command) {
			this.command = command;
		}
		
		/**
		 * Sets the text of the button.
		 */
		public void setText(final String text) {
			getUpFace().setText(text);
		}
	}
	
	public MessagePanel() {
		body = new FlexTable();
		initWidget(body);
		addStyleName(MdrResources.INSTANCE.css().messagePanel());

		FlexCellFormatter formatter = body.getFlexCellFormatter();
		formatter.setRowSpan(0, 0, 2);
		formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		
		image = new Image();
		image.setStyleName(MdrResources.INSTANCE.css().image());
		image.setVisible(false);
		body.setWidget(0, 0, image);
		
		formatter.addStyleName(0, 2, MdrResources.INSTANCE.css().text());
		
		commentLabel = new HTML();
		body.setWidget(1, 1, new ScrollPanel(commentLabel));
		commentLabel.setStyleName(MdrResources.INSTANCE.css().comment());
		
		formatter.setColSpan(2, 0, 2);
		formatter.setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		buttons = new HorizontalPanel();
		body.setWidget(2, 1, buttons);
	}

	/**
	 * Specifies if the spin wait icon is visible.
	 * @param visible <code>true</code> iff the icon is visible.
	 */
	public final void spinWait(boolean visible) {
		if (visible) {
			setImage(new Image(StandardImageBundle.INSTANCE.spin32Background()));
		}
		else {
			body.clearCell(0, 0);
		}
	}
	
	/**
	 * Shows the specified image.
	 * @param image is the image to show.
	 */
	public void setImage(Image image) {
		body.setWidget(0, 0, image);
	}
	
	/**
	 * Sets the text of the message.
	 * @param text is the text.
	 */
	public final void setText(String text) {
		body.setText(0, 2, text);
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
		if (isHTML) commentLabel.setHTML(comment);
		else commentLabel.setText(comment);
	}
	
	/**
	 * Adds a new button to the message box.
	 * 
	 * @return the new button.
	 */
	public final Button addButton() {
		Button button = new Button();
		buttons.add(button);
		return button;
	}
	
	/**
	 * Adds a new button with a specified text and command.
	 * 
	 * @param text is the text of the button.
	 * @param command is the command to perform when the button is pressed.
	 * @return the new button.
	 * 
	 * @see #addButton()
	 */
	public final Button addButton(String text, Command command) {
		Button button = addButton();
		button.setText(text);
		button.setCommand(command);
		return button;
	}
}
