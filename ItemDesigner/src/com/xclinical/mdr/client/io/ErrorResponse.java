package com.xclinical.mdr.client.io;

import com.google.gwt.core.client.JavaScriptObject;

public class ErrorResponse extends JavaScriptObject {

	protected ErrorResponse() {
	}

	public final native String getMessage() /*-{
		return this.message;
	}-*/;

	public final native String getDetails() /*-{
		return this.details;
	}-*/;

	public static final native ErrorResponse newClientError(String message, String details) /*-{
		return {
			message : message,
			details : details
		};
	}-*/;
}
