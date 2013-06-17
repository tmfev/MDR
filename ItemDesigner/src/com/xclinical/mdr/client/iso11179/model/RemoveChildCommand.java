package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class RemoveChildCommand extends Command {

	public static final String PATH = "removeChild";
	
	protected RemoveChildCommand() {
	}

	public static final native RemoveChildCommand newInstance(Item obj, Item child) /*-{
		return {
			obj : obj,
			child : child
		};
	}-*/;
}
