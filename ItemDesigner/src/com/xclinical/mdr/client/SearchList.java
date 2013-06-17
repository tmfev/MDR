package com.xclinical.mdr.client;


/**
 * Shows the items of the {@link HistoryItemProvider}.
 * 
 * @author ms@xclinical.com
 */
public class SearchList extends ItemList {

	private SearchItemProvider provider;
	
	public SearchList() {		
		super(true, new ItemRenderer(true, true));
	}
	
	public void setProvider(SearchItemProvider provider) {
		if (this.provider != null) {
			this.provider.removeDisplay(cellList);
			this.provider = null;
		}

		this.provider = provider;
		this.provider.addDisplay(cellList);
	}
	
}
