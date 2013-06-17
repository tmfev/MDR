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
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.DataElementConcept;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.reflect.Instantiable;

public class DataElementConceptEditor extends DesignatableItemEditor<DataElementConcept> {

	interface MyUiBindder extends UiBinder<Widget, DataElementConceptEditor> {}

	interface Driver extends SimpleBeanEditorDriver<DataElementConcept, DataElementConceptEditor> {}
	 
	private static MyUiBindder uiBinder = GWT.create(MyUiBindder.class);

	Driver driver = GWT.create(Driver.class);

	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	ItemLink<ConceptSystem> including;
	
	@UiField
	LazyItemList conceptualDomainList;

	@UiField
	LazyItemList representationList;
	
	public DataElementConceptEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		setDriver(driver);
	}

	@Instantiable(DataElementConcept.URN)
	public static class Factory extends AbstractGenericEditorFactory<DataElementConcept> {
		@Override
		protected GenericEditor<? super DataElementConcept> createSimple() {
			return new SimpleDataElementConceptEditor();
		}
		
		@Override
		public ItemEditor<DataElementConcept> createISO11179() {
			return new DataElementConceptEditor();
		}		
	}
}
