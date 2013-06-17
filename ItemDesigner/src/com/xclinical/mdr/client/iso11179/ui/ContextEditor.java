package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ContextEditor extends DesignatableItemEditor<Context> {
	
	interface Binder extends UiBinder<Widget, ContextEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Context, ContextEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList relevantDesignationList;
	

	public ContextEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Context.URN)
	public static class Factory extends AbstractGenericEditorFactory<Context> {
		@Override
		public GenericEditor<Context> createISO11179() {
			return new ContextEditor();
		}
		
		@Override
		protected GenericEditor<? super Context> createSimple() {
			return new SimpleContextEditor();
		}

		@Override
		protected GenericEditor<? super Context> createPreview() {
			return new ContextPreviewEditor();
		}
		
		@Override
		public LazyList getChildren(Context obj) {
			return obj.getRelevantDesignationList();
		}
	}
}
