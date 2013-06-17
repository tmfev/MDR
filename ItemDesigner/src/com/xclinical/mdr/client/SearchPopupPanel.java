package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.SearchCommand;
import com.xclinical.mdr.repository.Key;

public class SearchPopupPanel extends PopupPanel {

	interface Binder extends UiBinder<Widget, SearchPopupPanel> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	protected TextBox searchTerm;

	@UiField
	protected SearchList searchList;

	private final SearchItemProvider searchProvider = new SearchItemProvider();
	
	private final ItemLink<Item> link;
	
	private final Key type;
	
	@SuppressWarnings("unchecked")
	public SearchPopupPanel(ItemLink<? extends Item> link, Key type) {
		super(true);
		
		this.link = (ItemLink<Item>) link;
		this.type = type;
		
		setWidget(uiBinder.createAndBindUi(this));
		setSize("240pt", "200pt");
		
		searchList.setProvider(searchProvider);
		
		requery();
		
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				searchTerm.setFocus(true);
			}
		});
	}
	
	@UiFactory
	protected SearchList createList() {
		return new SearchList() {
			@Override
			public void onSelected(Item entity) {
				link.setValue(entity, true);
				hide();
			}
		};
	}
	
	private void requery() {
		String term = searchTerm.getText();
		searchProvider.setTerm(term, type, Item.SEARCH_BY_RATING);		
	}
	
	@UiHandler("searchTerm")
	protected void onSearchTermKeyUp(KeyUpEvent event) {
		requery();
	}	
}
