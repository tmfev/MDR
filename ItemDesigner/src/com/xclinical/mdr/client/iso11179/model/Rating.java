package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;

public final class Rating extends JavaScriptObject {

	public static final String URN = "urn:mdr:Rating";

	public static final String RATE = "rate";

	protected Rating() {
	}

	public final native float getAverageRating() /*-{
		return this.averageRating || 0;
	}-*/;

	public final native int getVotes() /*-{
		return this.votes || 0;
	}-*/;

	public final native int getValue() /*-{
		return this.value || 0;
	}-*/;

	public final native String getTarget() /*-{
		return this.target;
	}-*/;

	public static native Rating rate(String target, int value) /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.Rating::URN,
			value : value,
			target : target
		}
	}-*/;
}
