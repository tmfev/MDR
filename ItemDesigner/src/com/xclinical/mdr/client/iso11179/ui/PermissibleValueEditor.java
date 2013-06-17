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
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.PermissibleValue;
import com.xclinical.mdr.client.iso11179.model.ValueMeaning;
import com.xclinical.mdr.client.reflect.Instantiable;

public class PermissibleValueEditor extends DesignatableItemEditor<PermissibleValue> {
	
	interface Binder extends UiBinder<Widget, PermissibleValueEditor> {}

	interface Driver extends SimpleBeanEditorDriver<PermissibleValue, PermissibleValueEditor> {}

	@UiField
	ItemLink<Item> item;

	@UiField
	TextBox permittedValue;
	
	@UiField
	ItemLink<ValueMeaning> meaning;
	
	public PermissibleValueEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(PermissibleValue.URN)
	public static class Factory extends AbstractGenericEditorFactory<PermissibleValue> {
		@Override
		public ItemEditor<PermissibleValue> createISO11179() {
			return new PermissibleValueEditor();
		}		
	}
}
