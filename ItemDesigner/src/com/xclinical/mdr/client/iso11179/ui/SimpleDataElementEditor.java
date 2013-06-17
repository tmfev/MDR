package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.Implications;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.iso11179.model.DataElement;
import com.xclinical.mdr.client.iso11179.model.EditableDataElement;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.SaveDataElementCommand;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.ValueDomain;
import com.xclinical.mdr.repository.Key;

public class SimpleDataElementEditor extends
		EditableItemEditor<EditableDataElement, DataElement> {

	interface Binder extends UiBinder<Widget, SimpleDataElementEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableDataElement, SimpleDataElementEditor> {
	}

	@UiField
	ItemLink<ValueDomain> domain;

	Driver driver;

	public SimpleDataElementEditor() {
		super(SaveEditableItemCommand.<EditableDataElement>newInstance(EditableDataElement.newInstance()), SaveDataElementCommand.PATH);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}

	@Override
	protected void onEdit(EditableDataElement obj) {
		if (item.isNew()) {
			ValueDomain domain = (ValueDomain) Implications.optionalImplication(Key
					.parse(ValueDomain.URN));
			if (domain != null) {
				obj.setDomain(Item.ref(domain));
			}
		}
		
		driver.edit(obj);
	}

	@Override
	protected EditableDataElement flush() {
		return driver.flush();
	}
}
