package com.xclinical.mdr.client;

import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.HasItem;

/**
 * Shows the items of the {@link HistoryItemProvider}.
 * 
 * @author ms@xclinical.com
 */
public class FavoritesList extends ItemList implements DropTarget {

	public FavoritesList() {	
		super(true, new ItemRenderer(true, true));
		FavoritesItemProvider.get().addDataDisplay(cellList);
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
		FavoritesItemProvider.get().add(((HasItem)source).getItem());
	}

	@Override
	protected void remove(Item entity) {
		FavoritesItemProvider.get().remove(entity);
	}
}
