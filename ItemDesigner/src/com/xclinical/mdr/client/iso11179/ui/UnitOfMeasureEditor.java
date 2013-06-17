package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;
import com.xclinical.mdr.client.reflect.Instantiable;

public class UnitOfMeasureEditor extends ItemEditor<UnitOfMeasure> {
		
	interface Binder extends UiBinder<Widget, UnitOfMeasureEditor> {}

	interface Driver extends SimpleBeanEditorDriver<UnitOfMeasure, UnitOfMeasureEditor> {}
	 
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ConceptSystem> including;
	
	@UiField
	LazyItemList designationList;

	@UiField
	LazyItemList definitionList;
	
	@UiField
	LazyItemList dimensionalityList;
	
	public UnitOfMeasureEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(UnitOfMeasure.URN)
	public static class Factory extends AbstractGenericEditorFactory<UnitOfMeasure> {
		@Override
		public ItemEditor<UnitOfMeasure> createISO11179() {
			return new UnitOfMeasureEditor();
		}
		
		@Override
		protected GenericEditor<? super UnitOfMeasure> createBulk() {
			return new UnitOfMeasureListEditor();
		}
		
		@Override
		protected GenericEditor<? super UnitOfMeasure> createSimple() {
			return new SimpleUnitOfMeasureEditor();
		}
	}
}
