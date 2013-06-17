package com.xclinical.mdr.client;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.xclinical.mdr.client.iso11179.model.Item;

public class ItemLinkEditor<T extends Item> extends TakesValueEditor<T> implements Editor<T> {

	private ItemLinkEditor(ItemLink<T> link) {
		super(link);
	}
	
	static <T extends Item> ItemLinkEditor<T> of(ItemLink<T> link) {
		ItemLinkEditor<T> e = new ItemLinkEditor<T>(link);		
		return e;		
	}
}
