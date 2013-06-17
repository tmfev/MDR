package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class ClearFavouritesCommand extends Command {

	public static final String PATH = "clearFavourites";
	
	protected ClearFavouritesCommand() {
	}

	public static final native ClearFavouritesCommand newInstance() /*-{
		return {
		};
	}-*/;
}
