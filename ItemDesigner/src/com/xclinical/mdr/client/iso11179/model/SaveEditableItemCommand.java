package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public class SaveEditableItemCommand<I extends EditableItem> extends Command {

	public static final String PATH = "saveEditableItem";

	protected SaveEditableItemCommand() {
	}

	public final native I getItem() /*-{
		return this.item;
	}-*/;

	public final native void setItem(I item) /*-{
		this.item = item;
	}-*/;

	public static native <E extends EditableItem> SaveEditableItemCommand<E> newInstance(EditableItem item) /*-{
		return {
			item : item
		};
	}-*/;

}
