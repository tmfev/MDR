package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.ConceptualDomain;
import com.xclinical.mdr.client.iso11179.model.DataType;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;
import com.xclinical.mdr.client.iso11179.model.ValueDomain;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ValueDomainEditor extends DesignatableItemEditor<ValueDomain> {

	interface Binder extends UiBinder<Widget, ValueDomainEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ValueDomain, ValueDomainEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ConceptualDomain> meaning;
	
	@UiField
	ItemLink<DataType> dataType;
	
	@UiField
	TextBox format;
	
	@UiField
	IntegerBox maximumCharacterQuantity;
	
	@UiField
	ItemLink<UnitOfMeasure> unitOfMeasure;
	
	@UiField
	LazyItemList designationList;

	@UiField
	LazyItemList definitionList;

	@UiField
	LazyItemList memberList;
	
	public ValueDomainEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	
	@Instantiable(ValueDomain.URN)
	public static class Factory extends AbstractGenericEditorFactory<ValueDomain> {
		@Override
		public ItemEditor<ValueDomain> createISO11179() {
			return new ValueDomainEditor();
		}		
		
		@Override
		protected GenericEditor<? super ValueDomain> createSimple() {
			return new SimpleValueDomainEditor();
		}
	}
}
