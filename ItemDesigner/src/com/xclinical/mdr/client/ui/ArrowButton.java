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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.xclinical.mdr.client.res.StandardImageBundle;

public class ArrowButton extends PushButton {

	private static final String STYLENAME_DEFAULT = "mdr-ArrowButton";
	
	private static final StandardImageBundle images = (StandardImageBundle)GWT.create(StandardImageBundle.class);

	public enum Orientation {
		LEFT() {
			Image getImage() {
				return new Image(images.leftArrow());
			}
		},
		RIGHT() {
			Image getImage() {
				return new Image(images.rightArrow());
			}
		},
		DOWN() {
			Image getImage() {
				return new Image(images.downArrow());
			}
		},
		CLOSE() {
			Image getImage() {
				return new Image(images.close());
			}
		};
		
		abstract Image getImage();
	}
	
	{
		setStylePrimaryName(STYLENAME_DEFAULT);
	}
	
	public ArrowButton(Orientation orientation) {
		super(orientation.getImage());
	}

	public ArrowButton(Orientation orientation, ClickHandler clickHandler) {
		super(orientation.getImage(), clickHandler);
	}
}
