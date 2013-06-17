package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;

public final class CodeListElement extends JavaScriptObject {
	protected CodeListElement() {
	}

	public native String getId() /*-{
		return this.id;
	}-*/;

	public native void setId(String id) /*-{
		this.id = id;
	}-*/;
	
	public native final String getCode() /*-{
		return this.code;
	}-*/;

	public native void setCode(String code) /*-{
		this.code = code;
	}-*/;

	public native String getText() /*-{
		return this.text;
	}-*/;

	public native void setText(String text) /*-{
		this.text = text;
	}-*/;
	
	public static native CodeListElement newInstance() /*-{
		return {};
	}-*/;
	
	public static native CodeListElement newInstance(String code, String text) /*-{
		return {code : code, text: text};
	}-*/;
	
}