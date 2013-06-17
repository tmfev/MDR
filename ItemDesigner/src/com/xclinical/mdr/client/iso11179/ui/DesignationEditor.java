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
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.Designation;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.reflect.Instantiable;

public class DesignationEditor extends ItemEditor<Designation> {

	interface Binder extends UiBinder<Widget, DesignationEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Designation, DesignationEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	TextBox sign;
	
	@UiField
	ItemLink<LanguageIdentification> language;

	@UiField
	ItemLink<Item> designatedItem;
	
	public DesignationEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Designation.URN)
	public static class Factory extends AbstractGenericEditorFactory<Designation> {
		@Override
		public ItemEditor<Designation> createISO11179() {
			return new DesignationEditor();
		}		
	}	
}
