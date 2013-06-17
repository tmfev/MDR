package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ConceptSystemEditor extends DesignatableItemEditor<ConceptSystem> {

	interface Binder extends UiBinder<Widget, ConceptSystemEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ConceptSystem, ConceptSystemEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList includedAssertionList;

	@UiField
	LazyItemList memberList;
	
	public ConceptSystemEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	@Instantiable(ConceptSystem.URN)
	public static class Factory extends AbstractGenericEditorFactory<ConceptSystem> {
		@Override
		public ItemEditor<ConceptSystem> createISO11179() {
			return new ConceptSystemEditor();
		}
		
		@Override
		public LazyList getChildren(ConceptSystem obj) {
			return obj.getMemberList();
		}
	}
}
