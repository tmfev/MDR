package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.Implications;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.iso11179.model.DataType;
import com.xclinical.mdr.client.iso11179.model.EditableValueDomain;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.SaveValueDomainCommand;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;
import com.xclinical.mdr.client.iso11179.model.ValueDomain;
import com.xclinical.mdr.repository.Key;

public class SimpleValueDomainEditor extends EditableItemEditor<EditableValueDomain, ValueDomain> {

	interface Binder extends UiBinder<Widget, SimpleValueDomainEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableValueDomain, SimpleValueDomainEditor> {
	}
	
	@UiField
	ItemLink<DataType> dataType;

	@UiField
	TextBox format;
	
	@UiField
	IntegerBox maximumCharacterQuantity;

	@UiField
	ItemLink<UnitOfMeasure> unitOfMeasure;

	@UiField
	CodeListEditor codeList;

	Driver driver;
	
	public SimpleValueDomainEditor() {
		super(SaveEditableItemCommand.<EditableValueDomain>newInstance(EditableValueDomain.newInstance(ValueDomain.URN)), SaveValueDomainCommand.PATH);
		
		driver = GWT.create(Driver.class);		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		driver.initialize(this);		
	}

	@Override
	protected void onEdit(EditableValueDomain obj) {		
		if (item.isNew()) {
			DataType dataType = (DataType) Implications.optionalImplication(Key
					.parse(DataType.URN));
			if (dataType != null) {
				obj.setDataType(Item.ref(dataType));
			}
			
			UnitOfMeasure unitOfMeasure = (UnitOfMeasure) Implications.optionalImplication(Key
					.parse(UnitOfMeasure.URN));
			if (unitOfMeasure != null) {
				obj.setUnitOfMeasure(Item.ref(unitOfMeasure));
			}			
		}
		
		driver.edit(obj);
	}
	
	@Override
	protected EditableValueDomain flush() {
		return driver.flush();
	}	
}
