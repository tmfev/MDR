package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;


public final class LoginInfo extends JavaScriptObject {

	protected LoginInfo() {
	}

	public final native String getSession() /*-{
		return this.session;
	}-*/;

	public final native User getUser() /*-{
		return this.user;
	}-*/;
}
