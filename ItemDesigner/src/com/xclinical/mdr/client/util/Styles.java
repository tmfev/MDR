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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A type-safe wrapper to access the {@link DOM} utilities.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: 225 $
 */
public final class Styles {

	private static final String PIXELS = "px";
	
	private Styles() {}
	
	public static final String DISPLAY_INLINE = "inline";
	
	public static void setDisplay(UIObject obj, String display) {
		DOM.setStyleAttribute(obj.getElement(), "display", display);
	}
	
	public static void setPadding(UIObject obj, int padding) {
		DOM.setStyleAttribute(obj.getElement(), "padding", padding + PIXELS);
	}

	public static void setMargin(UIObject obj, int margin) {
		setMargin(obj, margin + PIXELS);
	}

	public static void setMargin(UIObject obj, int top, int right, int bottom, int left) {
		setMargin(obj, top + PIXELS + " " + right + PIXELS + " " + bottom + PIXELS + " " + right + PIXELS);
	}

	public static void setMargin(UIObject obj, String margin) {
		DOM.setStyleAttribute(obj.getElement(), "margin", margin);
	}

	public static void setBorder(UIObject obj, int margin) {
		setBorder(obj, margin + PIXELS);
	}

	public static void setBorder(UIObject obj, int top, int right, int bottom, int left) {
		setBorder(obj, top + PIXELS + " " + right + PIXELS + " " + bottom + PIXELS + " " + right + PIXELS);
	}

	public static void setBorder(UIObject obj, String margin) {
		DOM.setStyleAttribute(obj.getElement(), "border", margin);
	}
	
	public static final String POSITION_RELATIVE = "relative";
	
	public static void setPosition(UIObject obj, String position) {
		DOM.setStyleAttribute(obj.getElement(), "position", position);
	}

	public static final String FLOAT_RIGHT = "right";

	public static final String FLOAT_LEFT = "left";
	
	public static void setFloat(UIObject obj, String value) {
		DOM.setStyleAttribute(obj.getElement(), "float", value);
	}
	
	public static void setLeft(UIObject obj, int left) {
		DOM.setStyleAttribute(obj.getElement(), "left", left + PIXELS);
	}

	public static void setRight(UIObject obj, int right) {
		DOM.setStyleAttribute(obj.getElement(), "right", right + PIXELS);
	}

	public static void setTop(UIObject obj, int top) {
		DOM.setStyleAttribute(obj.getElement(), "top", top + PIXELS);
	}

	public static void setMaxWidth(UIObject obj, int width) {
		DOM.setStyleAttribute(obj.getElement(), "maxWidth", width + PIXELS);
	}

	public static void setMaxHeight(UIObject obj, int height) {
		DOM.setStyleAttribute(obj.getElement(), "maxHeight", height + PIXELS);
	}
	
	public static void setBottom(UIObject obj, int bottom) {
		DOM.setStyleAttribute(obj.getElement(), "bottom", bottom + PIXELS);
	}

	public static void setZIndex(UIObject obj, int order) {
		DOM.setStyleAttribute(obj.getElement(), "zIndex", String.valueOf(order));
	}

	public static void setBackgroundColor(UIObject obj, String color) {
		DOM.setStyleAttribute(obj.getElement(), "backgroundColor", color);
	}	
	
	public static void setTransparency(UIObject obj, float transparency) {
		DOM.setStyleAttribute(obj.getElement(), "transparency", String.valueOf(transparency));
	}	

	public static void setOverflow(UIObject obj, String value) {
		DOM.setStyleAttribute(obj.getElement(), "overflow", value);
	}	
}
