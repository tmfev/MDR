package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Loads an {@link EditableItem} by id.
 * 
 * @author ms@xclinical.com
 */
public final class LoadPreviewCommand extends Command {

	public static final String PATH = "loadPreview";

	protected LoadPreviewCommand() {
	}

	public static final native LoadPreviewCommand newInstance(Item item) /*-{
		return {
			id : item.id
		}
	}-*/;
}
