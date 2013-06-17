package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.Link;
import com.xclinical.mdr.client.iso11179.model.Relation;
import com.xclinical.mdr.client.reflect.Instantiable;

public class LinkEditor extends ItemEditor<Link> {

	interface Binder extends UiBinder<Widget, LinkEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Link, LinkEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ConceptSystem> assertor;

	@UiField
	ItemLink<Relation> relation;
	
	@UiField
	TextBox formula;

	@UiField
	LazyItemList termList;
	
	public LinkEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	@Instantiable(Link.URN)
	public static class Factory extends AbstractGenericEditorFactory<Link> {
		@Override
		public ItemEditor<Link> createISO11179() {
			return new LinkEditor();
		}
		
		@Override
		public LazyList getChildren(Link obj) {
			return obj.getTermList();
		}
	}
}
