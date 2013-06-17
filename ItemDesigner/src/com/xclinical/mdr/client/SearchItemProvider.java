package com.xclinical.mdr.client;

import java.util.Collections;
import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.iso11179.model.SearchCommand;
import com.xclinical.mdr.client.util.JsList;
import com.xclinical.mdr.repository.Key;

/**
 * Fills data consumers with {@link Item}s that match a specific term.
 * 
 * @author ms@xclinical.com
 */
public class SearchItemProvider {

	private String term;
	
	private Key type;

	private String algorithm;
	
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
			if (term != null) {
				final Range range = display.getVisibleRange();
				final int start = range.getStart();
				final int length = range.getLength();

				SearchCommand command = SearchCommand.fromTerm(term, type == null ? "" : type.toString(), algorithm, start, length);
				executor.invoke(SearchCommand.PATH, command);
			}
			else {
				updateRowCount(0, true);
				List<Item> e = Collections.emptyList();
				updateRowData(0, e);
			}
		}
	};
	
	public SearchItemProvider() {
		executor = new CommandExecutor<ResultList>();
		executor.setOnResult(new OnSuccessHandler<ResultList>() {					
			@Override
			public void onSuccess(ResultList result) {
				dataProvider.updateRowCount(result.getLength(), true);
				dataProvider.updateRowData(result.getStart(), new JsList<Item>(result.getElements()));						
			}
		});
	}

	/**
	 * Changes the filter associated with this provider.
	 * 
	 * @param filter is the new filter.
	 */
	public void setTerm(String term, Key type, String algorithm) {
		this.term = term;
		this.type = type;
		this.algorithm = algorithm;
		
		for (HasData<Item> display : dataProvider.getDataDisplays()) {
			display.setVisibleRangeAndClearData(display.getVisibleRange(), true);
		}
	}
	
	public String getTerm() {
		return term;
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
