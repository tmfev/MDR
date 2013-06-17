package com.xclinical.mdr.client;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.FindFirstCommand;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadListCommand;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.iso11179.model.User;
import com.xclinical.mdr.client.util.JsList;
import com.xclinical.mdr.client.util.LoginUtils;

public class HistoryItemProvider {

	private final ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	
	private static HistoryItemProvider instance;
	
	private HistoryItemProvider() {		
	}
	
	public static HistoryItemProvider get() {
		if (instance == null) {
			instance = new HistoryItemProvider();
		}
		
		return instance;
	}

	public void load() {
		CommandExecutor<ResultList> ex = new CommandExecutor<ResultList>();
		ex.setOnResult(new OnSuccessHandler<ResultList>() {
			@Override
			public void onSuccess(ResultList obj) {
				JsList<? extends Item> itemList = obj.getElementList();
				List<Item> history = dataProvider.getList();
				history.clear();
				history.addAll(itemList);
			}
		});
		
		ex.invoke(LoadListCommand.PATH, LoadListCommand.newInstance(LazyList.of(User.HISTORY, LoginUtils.getCurrentLoginInfo().getUser()), 0, FindFirstCommand.MAX_HISTORY));		
	}
	
	public void add(HasItem entity) {
		List<Item> history = dataProvider.getList();
		
		while (history.size() > FindFirstCommand.MAX_HISTORY - 1) {
			history.remove(history.size() - 1);
		}
		
		history.add(0, entity.getItem());
	}
	
	public void remove(Item entity) {
		dataProvider.getList().remove(entity);
	}
	
	public void clear() {
		dataProvider.getList().clear();
	}
	
	public void addDataDisplay(HasData<Item> display) {
		dataProvider.addDataDisplay(display);
	}
}
