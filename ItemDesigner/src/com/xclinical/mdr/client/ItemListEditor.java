package com.xclinical.mdr.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.xclinical.mdr.client.iso11179.model.Item;

public class ItemListEditor extends TakesValueEditor<JsArray<? extends Item>> implements Editor<JsArray<? extends Item>> {

	private ItemListEditor(ItemList list) {
		super(list);
	}
	
	static ItemListEditor of(ItemList list) {
		return new ItemListEditor(list);
	}
}
