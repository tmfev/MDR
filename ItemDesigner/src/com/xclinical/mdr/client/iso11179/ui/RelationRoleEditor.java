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
import com.xclinical.mdr.client.iso11179.model.RelationRole;
import com.xclinical.mdr.client.reflect.Instantiable;

public class RelationRoleEditor extends DesignatableItemEditor<RelationRole> {
	
	interface Binder extends UiBinder<Widget, RelationRoleEditor> {}

	interface Driver extends SimpleBeanEditorDriver<RelationRole, RelationRoleEditor> {}

	@UiField
	RatingPanel rating;

	@UiField
	ItemLink<Relation> source;

	@UiField
	LazyItemList linkEndList;
	
	@UiField
	ItemLink<Item> item;
	
	public RelationRoleEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(RelationRole.URN)
	public static class Factory extends AbstractGenericEditorFactory<RelationRole> {
		@Override
		public ItemEditor<RelationRole> createISO11179() {
			return new RelationRoleEditor();
		}		
	}
}
