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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

/**
 * A simple button with 2 faces.
 * 
 * @author michael@mictale.com
 */
public class SimpleButton extends Image implements MouseDownHandler, MouseUpHandler, MouseOverHandler, MouseOutHandler {

	private ImageResource up, down;
	
	private boolean isDown;
	
	public SimpleButton(ImageResource up, ImageResource down) {
		this.up = up;
		this.down = down;
		
		setResource(up);
		
		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		setResource(up);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		setResource(isDown ? down : up);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		isDown = false;
		setResource(up);
		DOM.releaseCapture(getElement());
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		isDown = true;
		setResource(down);
		DOM.setCapture(getElement());
	}
}
