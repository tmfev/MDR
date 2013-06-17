package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.io.Command;

/**
 * Saves a list of {@link Item}s and returns a {@link ItemListResult}.
 * 
 * @author ms@xclinical.com
 */
public final class SimpleDataTypeCommand extends Command {

	public static final String PATH = "saveDataType";
	
	protected SimpleDataTypeCommand() {		
	}

	public final native JsArray<? extends Item> getItem() /*-{
		return this.item;
	}-*/;

	public static native SimpleDataTypeCommand newInstance(Item item) /*-{
		return {
			item : item
		};
	}-*/;
}
