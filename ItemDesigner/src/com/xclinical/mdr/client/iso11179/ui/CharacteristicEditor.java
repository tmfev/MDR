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
import com.xclinical.mdr.client.iso11179.model.Characteristic;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.reflect.Instantiable;

public class CharacteristicEditor extends ItemEditor<Characteristic> {

	interface MyUiBindder extends UiBinder<Widget, CharacteristicEditor> {}

	interface Driver extends SimpleBeanEditorDriver<Characteristic, CharacteristicEditor> {}
	 
	private static MyUiBindder uiBinder = GWT.create(MyUiBindder.class);

	Driver driver = GWT.create(Driver.class);
	
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;
	
	@UiField
	LazyItemList designationList;
	
	@UiField
	LazyItemList conceptList;
	
	public CharacteristicEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		setDriver(driver);
	}

	@Instantiable(Characteristic.URN)
	public static class Factory extends AbstractGenericEditorFactory<Characteristic> {
		@Override
		public ItemEditor<Characteristic> createISO11179() {
			return new CharacteristicEditor();
		}

		@Override
		public LazyList getChildren(Characteristic obj) {
			return obj.getConceptList();
		}
	}
}
