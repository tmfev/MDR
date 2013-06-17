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
import com.xclinical.mdr.client.iso11179.model.Assertion;
import com.xclinical.mdr.client.iso11179.model.Concept;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LinkEnd;
import com.xclinical.mdr.client.reflect.Instantiable;

public class LinkEndEditor extends ItemEditor<LinkEnd> {

	interface Binder extends UiBinder<Widget, LinkEndEditor> {}

	interface Driver extends SimpleBeanEditorDriver<LinkEnd, LinkEndEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<Concept> end;

	@UiField
	ItemLink<Assertion> assertion;
	
	@UiField
	LazyItemList endRoleList;
	
	public LinkEndEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	@Instantiable(LinkEnd.URN)
	public static class Factory extends AbstractGenericEditorFactory<LinkEnd> {
		@Override
		public ItemEditor<LinkEnd> createISO11179() {
			return new LinkEndEditor();
		}
	}
}
