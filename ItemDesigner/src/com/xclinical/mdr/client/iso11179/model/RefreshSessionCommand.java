package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class RefreshSessionCommand extends Command {

	public static final String PATH = "refresh";
	
	protected RefreshSessionCommand() {
	}

	public final native String setSession(String session) /*-{
		this.session = session;
	}-*/;

	public static final native RefreshSessionCommand newInstance(String session) /*-{
		return {
			session : session
		};
	}-*/;
}
