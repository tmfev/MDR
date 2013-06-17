package com.xclinical.mdr.client;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.xclinical.mdr.client.iso11179.model.Item;

public class ScopeItemProvider {

	private final ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	
	private static ScopeItemProvider instance;
	
	private ScopeItemProvider() {		
	}
	
	public static ScopeItemProvider get() {
		if (instance == null) {
			instance = new ScopeItemProvider();
		}
		
		return instance;
	}
	
	public void add(Item entity) {
		if (!dataProvider.getList().contains(entity)) {
			dataProvider.getList().add(entity);
		}
	}
	
	public void remove(Item entity) {
		dataProvider.getList().remove(entity);		
	}
	
	public void clear() {
		dataProvider.getList().clear();
	}
	
	public List<Item> getItems() {
		return dataProvider.getList();
	}
	
	public void addDataDisplay(HasData<Item> display) {
		dataProvider.addDataDisplay(display);
	}
}
