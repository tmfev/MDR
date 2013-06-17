package com.xclinical.mdr.client;

import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;

public class ItemDragSource implements DragSource, HasItem {

	private final Item item;
	
	public ItemDragSource(Item item) {
		this.item = item;
	}
	
	@Override
	public Widget createDragImage() {
		return null;
	}

	@Override
	public void dragStart() {
	}

	@Override
	public void dragEnd(DropTarget target) {
	}

	@Override
	public Item getItem() {
		return item;
	}
}
