package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.ValueMeaning;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ValueMeaningEditor extends DesignatableItemEditor<ValueMeaning> {
	
	interface Binder extends UiBinder<Widget, ValueMeaningEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ValueMeaning, ValueMeaningEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	LazyItemList designationList;

	@UiField
	LazyItemList definitionList;
	
	@UiField
	LazyItemList representationList;
	
	public ValueMeaningEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(ValueMeaning.URN)
	public static class Factory extends AbstractGenericEditorFactory<ValueMeaning> {
		@Override
		public ItemEditor<ValueMeaning> createISO11179() {
			return new ValueMeaningEditor();
		}		
	}
}
