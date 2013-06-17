package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.EditableItem;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;

public class SimpleUnitOfMeasureEditor extends EditableItemEditor<EditableItem, UnitOfMeasure> {

	interface Binder extends UiBinder<Widget, SimpleUnitOfMeasureEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableItem, SimpleUnitOfMeasureEditor> {
	}

	private Driver driver;

	public SimpleUnitOfMeasureEditor() {
		super(SaveEditableItemCommand.newInstance(EditableItem.newInstance(UnitOfMeasure.URN)), SaveEditableItemCommand.PATH);
		
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

