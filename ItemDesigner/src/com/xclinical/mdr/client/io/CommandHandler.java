package com.xclinical.mdr.client.io;

import com.google.gwt.core.client.JavaScriptObject;

public interface CommandHandler<T extends JavaScriptObject> extends OnErrorHandler, OnSuccessHandler<T> {

}
