package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A remote method prototype.
 * 
 * @author ms@xclinical.com
 */
public final class RemoteMethod extends JavaScriptObject {

	protected RemoteMethod() {
	}

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public static final native RemoteMethod of(String name) /*-{
		return {
			name : name
		};
	}-*/;
}
