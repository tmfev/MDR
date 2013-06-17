package com.xclinical.mdr.client.io;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class CommandExecutor<T extends JavaScriptObject> {

	private Command command;

	private WebClient client;
	
	private OnSuccessHandler<T> onResult = new OnSuccessHandler<T>() {
		@Override
		public void onSuccess(T obj) {
		}
	}; 

	private OnErrorHandler onError = EMPTY_ERROR; 

	private static final OnErrorHandler EMPTY_ERROR = new OnErrorHandler() {
		@Override
		public void onError(ErrorResponse reason) {
			GWT.getUncaughtExceptionHandler().onUncaughtException(new Exception(reason.getMessage()));			
		}
	};
	
	public CommandExecutor() {
	}

	public void setOnResult(OnSuccessHandler<T> onResult) {
		this.onResult = onResult;
	}
	
	public void setOnError(OnErrorHandler onError) {
		this.onError = onError;
	}
	
	public void setCommand(Command command) {
		this.command = command;
	}
	
	public void invoke(String path, Command command) {
		setCommand(command);
		
		if (client != null) {
			client.cancel();
		}
		client = WebClient.create(path);
		client.sendRequest(this.command, onResult, onError);		
	}	
}
