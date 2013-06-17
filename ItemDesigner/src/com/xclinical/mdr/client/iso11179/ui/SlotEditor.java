package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.Slot;
import com.xclinical.mdr.client.reflect.Instantiable;

public class SlotEditor extends ItemEditor<Slot> {

	interface Binder extends UiBinder<Widget, SlotEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Slot, SlotEditor> {}

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	public SlotEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Instantiable(Slot.URN)
	public static class Factory extends AbstractGenericEditorFactory<Slot> {
		@Override
		public ItemEditor<Slot> createISO11179() {
			return new SlotEditor();
		}		
	}
}
