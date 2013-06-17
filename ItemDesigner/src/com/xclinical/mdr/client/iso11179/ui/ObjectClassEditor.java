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
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.ObjectClass;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ObjectClassEditor extends DesignatableItemEditor<ObjectClass> {

	interface Binder extends UiBinder<Widget, ObjectClassEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ObjectClass, ObjectClassEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList conceptList;
	
	public ObjectClassEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(ObjectClass.URN)
	public static class Factory extends AbstractGenericEditorFactory<ObjectClass> {
		@Override
		public ItemEditor<ObjectClass> createISO11179() {
			return new ObjectClassEditor();
		}
		
		@Override
		public LazyList getChildren(ObjectClass obj) {
			return obj.getConceptList();
		}
	}
}
