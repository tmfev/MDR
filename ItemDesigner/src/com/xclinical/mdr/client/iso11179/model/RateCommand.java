package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public final class RateCommand extends Command {

	public static final String PATH = "rate";
	
	protected RateCommand() {
	}

	public final native Rating getRating() /*-{
		return this.rating;
	}-*/;
	
	public static final native RateCommand newInstance(Rating rating) /*-{
		return {
			rating: rating
		};
	}-*/;
}
