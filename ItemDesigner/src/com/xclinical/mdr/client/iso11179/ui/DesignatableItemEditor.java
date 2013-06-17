package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.uibinder.client.UiField;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.iso11179.model.Item;

public abstract class DesignatableItemEditor<T extends Item> extends ItemEditor<T> {

	@UiField
	LazyItemList designationList;

	@UiField
	LazyItemList definitionList;

	@UiField
	LazyItemList classifierList;
	
	public DesignatableItemEditor() {
	}	
}
