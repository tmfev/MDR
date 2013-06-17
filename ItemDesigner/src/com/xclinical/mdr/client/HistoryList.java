package com.xclinical.mdr.client;

import com.xclinical.mdr.client.iso11179.model.Item;


/**
 * Shows the items of the {@link HistoryItemProvider}.
 * 
 * @author ms@xclinical.com
 */
public class HistoryList extends ItemList {

	public HistoryList() {		
		super(true, new ItemRenderer(true, true));
		HistoryItemProvider.get().addDataDisplay(cellList);
	}
	
	@Override
	protected void remove(Item entity) {
		HistoryItemProvider.get().remove(entity);
	}
}
