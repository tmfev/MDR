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
import com.xclinical.mdr.client.iso11179.model.Assertion;
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.reflect.Instantiable;

public class AssertionEditor extends ItemEditor<Assertion> {
		
	interface Binder extends UiBinder<Widget, AssertionEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Assertion, AssertionEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ConceptSystem> assertor;
	
	@UiField
	TextBox formula;

	@UiField
	LazyItemList termList;
	
	public AssertionEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Assertion.URN)
	public static class Factory extends AbstractGenericEditorFactory<Assertion> {
		@Override
		public ItemEditor<Assertion> createISO11179() {
			return new AssertionEditor();
		}		
	}	
}
