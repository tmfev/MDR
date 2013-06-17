package com.xclinical.mdr.client;

import java.util.Collections;
import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.ErrorResponse;
import com.xclinical.mdr.client.io.OnErrorHandler;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Designation;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadListCommand;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.util.JsList;

/**
 * Fills data consumers with {@link Item}s that match a specific term.
 * 
 * @author ms@xclinical.com
 */
public class QueryItemProvider {

	private String query;
	
	private CommandExecutor<ResultList> executor;

	private static final ProvidesKey<Item> KEY_PROVIDER = new ProvidesKey<Item>() {
		@Override
		public Object getKey(Item item) {
			return item == null ? null : item.getId();
		}
	};
	
	private final AsyncDataProvider<Item> dataProvider = new AsyncDataProvider<Item>(KEY_PROVIDER) {		
		@Override
		protected void onRangeChanged(HasData<Item> display) {
			if (query != null) {
				final Range range = display.getVisibleRange();
				final int start = range.getStart();
				final int length = range.getLength();

				executor.invoke(LoadListCommand.PATH, LoadListCommand.newInstance(LazyList.query(query), start, length));
			}
			else {
				updateRowCount(0, true);
				List<Item> e = Collections.emptyList();
				updateRowData(0, e);
			}
		}
	};
	
	public QueryItemProvider() {
		executor = new CommandExecutor<ResultList>();
		executor.setOnResult(new OnSuccessHandler<ResultList>() {					
			@Override
			public void onSuccess(ResultList result) {
				dataProvider.updateRowCount(result.getLength(), true);
				dataProvider.updateRowData(result.getStart(), new JsList<Item>(result.getElements()));						
			}
		});
		executor.setOnError(new OnErrorHandler() {
			@Override
			public void onError(ErrorResponse error) {
				dataProvider.updateRowCount(1, true);
				Designation d = Designation.newDesignation(error.getMessage(), null, null);
				d.setDisplayName(error.getMessage());
				dataProvider.updateRowData(0, Collections.<Item>singletonList(d));						
			}
		});
	}

	/**
	 * Changes the filter associated with this provider.
	 * 
	 * @param filter is the new filter.
	 */
	public void setQuery(String query) {
		this.query = query;
		
		for (HasData<Item> display : dataProvider.getDataDisplays()) {
			display.setVisibleRangeAndClearData(display.getVisibleRange(), true);
		}
	}
	
	/**
	 * Adds a view to this provider.
	 * 
	 * @param display is the view to add.
	 */
	public void addDisplay(HasData<Item> display) {
		dataProvider.addDataDisplay(display);
	}
	
	/**
	 * Removes a view from this provider.
	 * 
	 * @param display is the view to remove.
	 */
	public void removeDisplay(HasData<Item> display) {
		dataProvider.removeDataDisplay(display);
	}
}
