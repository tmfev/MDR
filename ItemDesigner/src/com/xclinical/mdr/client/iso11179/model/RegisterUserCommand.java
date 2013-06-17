package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class RegisterUserCommand extends Command {

	public static final String PATH = "register";
	
	protected RegisterUserCommand() {
	}

	public static final native RegisterUserCommand newInstance() /*-{
		return {
		};
	}-*/;
}
