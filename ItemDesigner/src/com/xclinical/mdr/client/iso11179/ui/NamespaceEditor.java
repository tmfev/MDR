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
import com.xclinical.mdr.client.iso11179.model.Namespace;
import com.xclinical.mdr.client.reflect.Instantiable;

public class NamespaceEditor extends ItemEditor<Namespace> {

	interface Binder extends UiBinder<Widget, NamespaceEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Namespace, NamespaceEditor> {}
	 
	@UiField
	ItemLink<Item> item;
	
	@UiField
	TextBox namespaceSchemeReference;
	
	public NamespaceEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Namespace.URN)
	public static class Factory extends AbstractGenericEditorFactory<Namespace> {
		@Override
		public ItemEditor<Namespace> createISO11179() {
			return new NamespaceEditor();
		}		
	}
}
