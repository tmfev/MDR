package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class AddFavouriteCommand extends Command {

	public static final String PATH = "addFavourite";
	
	protected AddFavouriteCommand() {
	}

	public static final native AddFavouriteCommand newInstance(Item obj) /*-{
		return {
			obj : obj
		};
	}-*/;
}
