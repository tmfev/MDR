package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public final class SaveItemCommand<T extends Item> extends Command {

	public static final String PATH = "saveItem";

	protected SaveItemCommand() {
	}

	public static native <E extends Item> SaveItemCommand<E> newInstance(Item item) /*-{
		return {
			item : item
		};
	}-*/;

}