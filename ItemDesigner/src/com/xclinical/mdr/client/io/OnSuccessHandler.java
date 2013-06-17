package com.xclinical.mdr.client.io;

import com.google.gwt.core.client.JavaScriptObject;

public interface OnSuccessHandler<T extends JavaScriptObject> {
	public void onSuccess(T obj);
}