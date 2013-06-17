package com.xclinical.mdr.client;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadListCommand;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.util.JsList;

/**
 * A data provider that loads contents from a {@link LazyList}.
 * 
 * @author ms@xclinical.com
 */
public class LazyListDataProvider extends AsyncDataProvider<Item> {

	private final LazyList list;
	
	public LazyListDataProvider(LazyList list) {
		this.list = list;		
	}
	
	@Override
	protected void onRangeChanged(final HasData<Item> display) {
		if (list.isEmpty()) {
			display.setRowCount(0);
		}
		else {
			final Range range = display.getVisibleRange();
			final int start = range.getStart();
			final int length = range.getLength();
			
			CommandExecutor<ResultList> ex = new CommandExecutor<ResultList>();
			ex.setOnResult(new OnSuccessHandler<ResultList>() {
				@Override
				public void onSuccess(ResultList obj) {
					JsList<? extends Item> itemList = obj.getElementList();
					onDataChanged(display, start, obj.getLength(), itemList);
				}
			});
			
			ex.invoke(LoadListCommand.PATH, LoadListCommand.newInstance(list, start, length));
		}
	}
	
	public LazyList getList() {
		return list;
	}
	
	protected void onDataChanged(HasData<Item> display, int start, int size, JsList<? extends Item> items) {
		display.setRowData(start, items);
		
		if (start == 0) {
			display.setRowCount(size, true);
		}		
	}
}
