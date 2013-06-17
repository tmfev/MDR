package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class AddChildCommand extends Command {

	public static final String PATH = "addChild";
	
	protected AddChildCommand() {
	}

	public static final native AddChildCommand newInstance(Item obj, Item child) /*-{
		return {
			obj : obj,
			child : child
		};
	}-*/;
}
