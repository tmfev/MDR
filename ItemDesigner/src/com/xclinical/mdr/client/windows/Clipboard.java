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
package com.xclinical.mdr.client.windows;

import com.google.gwt.user.client.ui.IsWidget;

public class Clipboard {

	private WindowManager wm;
	
	private Object data;
	
	public Clipboard(WindowManager wm) {
		this.wm = wm;
	}
	
	private HasClipboardData getTarget() {
		Window active = wm.getActiveWindow();
		IsWidget body = active.getBody();
		if (body instanceof HasClipboardData) {
			return (HasClipboardData)body;
		}
		else {
			return null;
		}
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public void cut() {		
	}

	public void copy() {
		HasClipboardData target = getTarget();
		target.onCopy(this);
	}
	
	public void paste() {		
		HasClipboardData target = getTarget();
		target.onPaste(this);
	}	

	public void remove() {		
		HasClipboardData target = getTarget();
		target.onRemove(this);
	}	
}
