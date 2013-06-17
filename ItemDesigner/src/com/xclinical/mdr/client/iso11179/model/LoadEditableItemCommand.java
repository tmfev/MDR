package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Loads an {@link EditableItem} by id.
 * 
 * @author ms@xclinical.com
 */
public final class LoadEditableItemCommand extends Command {

	public static final String PATH = "loadEditableItem";

	protected LoadEditableItemCommand() {
	}

	public static final native LoadEditableItemCommand newInstance(Item item) /*-{
		return {
			id : item.id
		}
	}-*/;
}
