package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.Concept;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ConceptEditor extends DesignatableItemEditor<Concept> {

	interface Binder extends UiBinder<Widget, ConceptEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Concept, ConceptEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList assertionList;

	public ConceptEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	@Instantiable(Concept.URN)
	public static class Factory extends AbstractGenericEditorFactory<Concept> {
		@Override
		protected GenericEditor<? super Concept> createBrowser() {
			return new ConceptBrowser();
		}
		
		@Override
		public ItemEditor<Concept> createISO11179() {
			return new ConceptEditor();
		}
		
		@Override
		public LazyList getChildren(Concept obj) {
			return obj.getAssertionList();
		}
	}
}
