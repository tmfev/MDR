package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.DataElement;
import com.xclinical.mdr.client.iso11179.model.DataElementConcept;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.ValueDomain;
import com.xclinical.mdr.client.reflect.Instantiable;

public class DataElementEditor extends DesignatableItemEditor<DataElement> {
	
	interface MyUiBindder extends UiBinder<Widget, DataElementEditor> {}

	interface Driver extends SimpleBeanEditorDriver<DataElement, DataElementEditor> {}
	 
	private static MyUiBindder uiBinder = GWT.create(MyUiBindder.class);

	Driver driver = GWT.create(Driver.class);
	
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ValueDomain> domain;
	
	@UiField
	ItemLink<DataElementConcept> meaning;

	@UiField
	IntegerBox precision;
	
	public DataElementEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		setDriver(driver);
	}

	@Instantiable(DataElement.URN)
	public static class Factory extends AbstractGenericEditorFactory<DataElement> {
		@Override
		public ItemEditor<DataElement> createISO11179() {
			return new DataElementEditor();
		}		
		
		@Override
		protected GenericEditor<? super DataElement> createSimple() {
			return new SimpleDataElementEditor();
		}
		
		@Override
		protected GenericEditor<? super DataElement> createPreview() {
			return new DataElementPreviewEditor();
		}
	}
}
