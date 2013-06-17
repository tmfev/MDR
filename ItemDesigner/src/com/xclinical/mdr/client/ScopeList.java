package com.xclinical.mdr.client;

import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.HasItem;

/**
 * Shows the items of the {@link ScopeItemProvider}.
 * 
 * @author ms@xclinical.com
 */
public class ScopeList extends ItemList implements DropTarget {

	public ScopeList() {		
		ScopeItemProvider.get().addDataDisplay(cellList);
	}
	
	@Override
	protected void onAttach() {
		DragAndDrop.addTarget(this);
		super.onAttach();
	}
	
	@Override
	protected void onDetach() {
		DragAndDrop.removeTarget(this);
		super.onDetach();
	}
	
	@Override
	public boolean canDrop(DragSource source) {
		return source instanceof HasItem;
	}

	@Override
	public void cancelDrop() {
	}
	
	@Override
	public void drop(DragSource source, Widget drag) {
		ScopeItemProvider.get().add(((HasItem)source).getItem());
	}

	@Override
	protected void remove(Item entity) {
		ScopeItemProvider.get().remove(entity);
	}
}
