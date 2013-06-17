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
package com.xclinical.mdr.client.res;

import com.google.gwt.resources.client.CssResource;

public interface MdrCss extends CssResource {

	@ClassName("mdr-Link")
	String link();
	
	@ClassName("mdr-ItemFieldLabel")
	String itemFieldLabel();
	
	@ClassName("mdr-HTMLItemFieldLabel")
	String htmlItemFieldEditor();
	
	@ClassName("mdr-StringItemFieldLabel")
	String stringItemFieldEditor();
	
	@ClassName("mdr-MessagePanel")
	String messagePanel();
	
	@ClassName("mdr-MessageBox")	
	String messageBox();
	
	String image();
	
	String text();
	
	String comment();
	
	@ClassName("mdr-NoSel")	
	String nosel();

	@ClassName("mdr-Popup")	
	String popup();

	@ClassName("mdr-FadeGray")	
	String fadeGray();

	@ClassName("mdr-InsetBorder")	
	String insetBorder();

	@ClassName("mdr-PreviewLabel")	
	String previewLabel();

	@ClassName("mdr-Dragbox")	
	String dragBox();

	@ClassName("mdr-Dragbox-droppable")	
	String dragBoxDroppable();
}
