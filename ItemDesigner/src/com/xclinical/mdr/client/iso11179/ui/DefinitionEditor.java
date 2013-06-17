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
import com.xclinical.mdr.client.iso11179.model.Definition;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.reflect.Instantiable;

public class DefinitionEditor extends ItemEditor<Definition> {

	interface Binder extends UiBinder<Widget, DefinitionEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Definition, DefinitionEditor> {}
	 
	@UiField
	ItemLink<Item> item;

	@UiField
	TextBox text;
	
	@UiField
	ItemLink<LanguageIdentification> language;

	@UiField
	ItemLink<Item> definedItem;
	
	public DefinitionEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Definition.URN)
	public static class Factory extends AbstractGenericEditorFactory<Definition> {
		@Override
		public ItemEditor<Definition> createISO11179() {
			return new DefinitionEditor();
		}		
	}	
}
