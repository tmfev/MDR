package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.EditableItem;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;

public class SimpleContextEditor extends EditableItemEditor<EditableItem, Context> {

	interface Binder extends UiBinder<Widget, SimpleContextEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableItem, SimpleContextEditor> {
	}

	private Driver driver;

	public SimpleContextEditor() {
		super(SaveEditableItemCommand.newInstance(EditableItem.newInstance(Context.URN)), SaveEditableItemCommand.PATH);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}

	@Override
	protected void onEdit(EditableItem obj) {
		driver.edit(obj);
	}
	
	@Override
	protected EditableItem flush() {
		return driver.flush();
	}
}
