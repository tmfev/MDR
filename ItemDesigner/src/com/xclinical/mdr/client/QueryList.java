package com.xclinical.mdr.client;


/**
 * Shows the items of the {@link HistoryItemProvider}.
 * 
 * @author ms@xclinical.com
 */
public class QueryList extends ItemList {

	private QueryItemProvider provider;
	
	public QueryList() {		
		super(true, new ItemRenderer(true, true));
	}
	
	public void setProvider(QueryItemProvider provider) {
		if (this.provider != null) {
			this.provider.removeDisplay(cellList);
			this.provider = null;
		}

		this.provider = provider;
		this.provider.addDisplay(cellList);
	}
	
}
