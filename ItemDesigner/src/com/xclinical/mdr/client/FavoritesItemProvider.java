package com.xclinical.mdr.client;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.xclinical.mdr.client.iso11179.model.Item;

public class FavoritesItemProvider {

	private final ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	
	private static FavoritesItemProvider instance;
	
	private FavoritesItemProvider() {		
	}
	
	public static FavoritesItemProvider get() {
		if (instance == null) {
			instance = new FavoritesItemProvider();
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
	
	public void addDataDisplay(HasData<Item> display) {
		dataProvider.addDataDisplay(display);
	}
}
