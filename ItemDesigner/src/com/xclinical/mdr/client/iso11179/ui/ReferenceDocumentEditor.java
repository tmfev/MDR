package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.ItemDesigner;
import com.xclinical.mdr.client.ItemEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.ImportDocumentCommand;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.iso11179.model.ReferenceDocument;
import com.xclinical.mdr.client.reflect.Instantiable;

public class ReferenceDocumentEditor extends DesignatableItemEditor<ReferenceDocument> {

	interface Binder extends UiBinder<Widget, ReferenceDocumentEditor> {}

	interface Driver extends SimpleBeanEditorDriver<ReferenceDocument, ReferenceDocumentEditor> {}
	 
	@UiField
	RatingPanel rating;
	
	@UiField
	ItemLink<Item> item;

	@UiField
	TextBox identifier;

	@UiField
	TextBox typeDescription;

	@UiField
	ItemLink<LanguageIdentification> language;
	
	@UiField
	TextBox title;
	
	@UiField
	TextBox uri;
	
	@UiField
	Anchor link;
	
	@UiField
	@Ignore
	ItemLink<ReferenceDocument> format;
	
	public ReferenceDocumentEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		setDriver(GWT.<Driver>create(Driver.class));
	}

	@Override
	protected void onOpen(ReferenceDocument entity) {
		String url = GWT.getModuleBaseURL();
		url = url.substring(0, url.length() - 1);		
		url += entity.getUri();
		link.setText(url);
		link.setTarget(url);
		
		super.onOpen(entity);
	}

	@UiHandler("link")
	public void onLinkClicked(ClickEvent event) {
		Window.open(link.getTarget(), "_blank", "");
	}
	
	@UiHandler("importDocument")
	public void onImportClicked(ClickEvent event) {		
		ImportDocumentCommand command = ImportDocumentCommand.newInstance(item.getItem(), format.getItem());
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				ItemDesigner.get().open(obj, Mode.DEFAULT, false);
			}
		});
		executor.invoke(ImportDocumentCommand.PATH, command);								
	}
	
	@Instantiable(ReferenceDocument.URN)
	public static class Factory extends AbstractGenericEditorFactory<ReferenceDocument> {
		@Override
		public ItemEditor<ReferenceDocument> createISO11179() {
			return new ReferenceDocumentEditor();
		}		
	}	
}
