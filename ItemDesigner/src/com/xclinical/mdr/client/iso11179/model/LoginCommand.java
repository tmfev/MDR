package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class LoginCommand extends Command {

	public static final String PATH = "login";
	
	protected LoginCommand() {
	}

	public final native String setEmail(String email) /*-{
		this.email = email;
	}-*/;

	public final native String getEmail() /*-{
		return this.email;
	}-*/;

	public final native String setPassword(String password) /*-{
		this.password = password;
	}-*/;

	public final native String getPassword() /*-{
		return this.password;
	}-*/;

	public static final native LoginCommand newInstance(String email, String password) /*-{
		return {
			email : email,
			password : password
		};
	}-*/;
}
