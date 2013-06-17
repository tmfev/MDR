package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JsArray;

public class EditableDataType extends EditableItem {

	protected EditableDataType() {
	}

	public final native JsArray<? extends Item> getItems() /*-{
		return this.items;
	}-*/;

	public static native SimpleDataTypeCommand newInstance(Item item) /*-{
		return {
			items : [ item ]
		};
	}-*/;

	public static native EditableDataType newInstance(JsArray<? extends Item> item) /*-{
		return {
			items : item
		};
	}-*/;

}
