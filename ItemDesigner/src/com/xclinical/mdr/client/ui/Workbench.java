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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.ui.MessagePanel.Button;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.service.InvalidSessionException;
import com.xclinical.mdr.shared.Strings;

/**
 * Provides access to the root of all application UI elements.
 *
 * There is a single workbench existing per application.
 * 
 * A workbench comprises a set of {@link Perspective}s which can be activated to establish
 * a specific layout.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: 275 $
 */
public class Workbench {

	public static final String DEFAULT_PERSPECTIVE = "Default";
	
	private final String name;
	
	private Perspective currentPerspective;
	
	private Map<String, Perspective> perspectives = new HashMap<String, Perspective>();
	
	private RootLayoutPanel rootPanel;
	
	private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
		public void onUncaughtException(final Throwable e) {
			if (e instanceof InvalidSessionException) {
				final PopupDialog<SignInWidget> dialog = new PopupDialog<SignInWidget>();
				LoginUtils.clearSession();
				SignInWidget signIn = new SignInWidget(Platform.CONSTANTS.sessionExpiredMessage());				
				dialog.setBody(signIn);
				Platform.getWorkbench().showPopup(dialog);
			}
			else {
				final MessageBox messageBox = new MessageBox(Platform.CONSTANTS.exceptionText());
				final Button button = messageBox.addButton(false);
				Command moreCommand = new Command() {
					boolean more = false;
					public void execute() {
						if (more) {
							button.setText(Platform.CONSTANTS.exceptionLessInfo());
							messageBox.setComment("<pre>" + Strings.toString(e) + "</pre>", true);
						}
						else {
							button.setText(Platform.CONSTANTS.exceptionMoreInfo());
							messageBox.setComment(e.getLocalizedMessage());							
						}
	
						// Re-position
						messageBox.showPopup();
	
						// Toggle state
						more = !more;
					}
				};
				
				button.setCommand(moreCommand);
				moreCommand.execute();
				messageBox.addButton(Platform.CONSTANTS.dialogClose(), null);
				messageBox.showPopup();
			}
		}
	};

	/**
	 * Initializes a new object.
	 */
	public Workbench(String name) {
		this.name = name;
		
		GWT.setUncaughtExceptionHandler(uncaughtExceptionHandler);

		rootPanel = RootLayoutPanel.get();
		
		addPerspective(DEFAULT_PERSPECTIVE, new AbstractPerspective() {
			public void attach(Workbench wb) {
			}
		});
		
		activatePerspective(DEFAULT_PERSPECTIVE);
	}

	/**
	 * Retrieves the name of the workbench which is usually the name of the application
	 * that is currently running.
	 * 
	 * @return the name of the workbench.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a perspective with a specific name.
	 * 
	 * @param name is the name of the perspective.
	 * @param perspective is the perspective.
	 */
	public void addPerspective(String name, Perspective perspective) {
		perspectives.put(name, perspective);
	}
	
	/**
	 * Activates the perspective with the specified name.
	 * 
	 * @param perspective is the name of the perspective to initialize.
	 */
	public void activatePerspective(String perspective) {
		Perspective newPerspective = perspectives.get(perspective);
		if (newPerspective == null) {
			throw new IllegalArgumentException("Unknown perspective: " + perspective);
		}
		
		// hideLoadingMessage();

		rootPanel.clear();
		
		if (currentPerspective != null) {
			currentPerspective.detach();
		}
		
		currentPerspective = newPerspective;
		currentPerspective.attach(this);
	}
	
	public Perspective getCurrentPerspective() {
		return currentPerspective;
	}
	
	/**
	 * Adds a drop target.
	 * @param w is the drop target.
	 */
	public void addDropTarget(Widget w) {
		DragAndDrop.addTarget(w);
	}

	/**
	 * Adds a part to the current workbench.
	 * 
	 * @param part is the part to add.
	 */
	public void addPart(Widget part) {
		rootPanel.add(part);
	}
	
	private void hideLoadingMessage() {
		Element elm = DOM.getElementById("loadingMessage");
		DOM.setStyleAttribute(elm, "display", "none");
	}
	
	public void showPopup(final PopupPanel panel) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {			
			@Override
			public void execute() {
				currentPerspective.showPopup(panel);
			}
		});
	}
}
