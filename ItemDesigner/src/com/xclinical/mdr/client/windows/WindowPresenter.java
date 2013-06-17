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

import java.util.Collection;

/**
 * Presents windows.
 * 
 * Usually, an instance owns a region, e.g. a widget and creates child widgets that each represents a single
 * window.
 * 
 * @author ms
 *
 */
public interface WindowPresenter {

	void addWindow(Window wnd);
	
	void removeWindow(Window wnd);

	void onBodyChanged(Window window);

	void onTitleChanged(Window window);
	
	void setActive(Window window);
	
	Window getActiveWindow();

	boolean isWindowActive(Window window);

	void retain(Window wnd, WindowPresenter dock);

	Collection<Window> allWindows();
}
