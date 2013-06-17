package com.xclinical.mdr.client.iso11179.model;


public final class Authentication extends Item {

	public static final String URN = "urn:mdr:Authentication";

	public static final String LOGIN = "login";

	public static final String REFRESH = "refresh";

	protected Authentication() {
	}

	public static final native Authentication newInstance() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.Authentication::URN
		};
	}-*/;
}
