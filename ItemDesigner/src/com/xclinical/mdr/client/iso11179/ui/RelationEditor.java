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
import com.xclinical.mdr.client.iso11179.model.Relation;
import com.xclinical.mdr.client.reflect.Instantiable;

public class RelationEditor extends DesignatableItemEditor<Relation> {

	interface Binder extends UiBinder<Widget, RelationEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Relation, RelationEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList roleList;

	@UiField
	LazyItemList linkList;
	
	public RelationEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Relation.URN)
	public static class Factory extends AbstractGenericEditorFactory<Relation> {
		@Override
		public ItemEditor<Relation> createISO11179() {
			return new RelationEditor();
		}		
	}
}
