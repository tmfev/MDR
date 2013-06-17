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
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.ConceptualDomain;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ConceptualDomainEditor extends DesignatableItemEditor<ConceptualDomain> {
	
	interface Binder extends UiBinder<Widget, ConceptualDomainEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ConceptualDomain, ConceptualDomainEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	TextBox description;
	
	@UiField
	LazyItemList usageList;
	
	@UiField
	LazyItemList representationList;
	
	@UiField
	LazyItemList memberList;
	
	public ConceptualDomainEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));	
		setDriver(GWT.<Driver>create(Driver.class));		
	}

	@Instantiable(ConceptualDomain.URN)
	public static class Factory extends AbstractGenericEditorFactory<ConceptualDomain> {
		@Override
		public ItemEditor<ConceptualDomain> createISO11179() {
			return new ConceptualDomainEditor();
		}
		
		@Override
		public LazyList getChildren(ConceptualDomain obj) {
			return obj.getMemberList();
		}		
	}
}
