package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.DataType;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.reflect.Instantiable;

public class DataTypeEditor extends ItemEditor<DataType> {
	
	interface Binder extends UiBinder<Widget, DataTypeEditor> {}

	interface Driver extends SimpleBeanEditorDriver<DataType, DataTypeEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	TextBox name;

	@UiField
	TextBox description;
	
	@UiField
	TextBox schemeReference;
	
	public DataTypeEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(DataType.URN)
	public static class Factory extends AbstractGenericEditorFactory<DataType> {
		@Override
		public ItemEditor<DataType> createISO11179() {
			return new DataTypeEditor();
		}		
		
		@Override
		protected GenericEditor<? super DataType> createBulk() {
			return new DataTypeListCommandEditor();
		}
		
		@Override
		protected GenericEditor<? super DataType> createSimple() {
			return new SimpleDataTypeEditor();
		}
	}
}
