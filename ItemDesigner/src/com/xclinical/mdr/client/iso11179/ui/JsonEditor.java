package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.iso11179.model.Item;

public class JsonEditor<T extends Item> extends Composite implements LeafValueEditor<T>, GenericEditor<T> {

	public interface Binder extends UiBinder<Widget, JsonEditor<?>> {
		Binder B = GWT.create(Binder.class);
	}

	@UiField
	TextArea editor;
	
	private T value;
	
	public JsonEditor() {
		initWidget(Binder.B.createAndBindUi(this));
	}

	@Override
	public void setValue(T value) {
		this.value = value;
		editor.setText(value.getJson());
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void open(T obj) {
		setValue(obj);
	}

	@Override
	public void save() {
	}
}
