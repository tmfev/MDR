package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.DataElement;
import com.xclinical.mdr.client.iso11179.model.DataElementPreview;
import com.xclinical.mdr.client.iso11179.model.LoadPreviewCommand;

public class DataElementPreviewEditor extends Composite implements GenericEditor<DataElement> {

	interface Binder extends UiBinder<Widget, DataElementPreviewEditor> {}

	@UiField
	DataElementPreviewWidget preview;
	
	private DataElement obj;
	
	public DataElementPreviewEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
	}

	@Override
	public void open(DataElement obj) {
		this.obj = obj;
		
		LoadPreviewCommand command = LoadPreviewCommand.newInstance(obj);
		CommandExecutor<DataElementPreview> ex = new CommandExecutor<DataElementPreview>();
		ex.setOnResult(new OnSuccessHandler<DataElementPreview>() {
			public void onSuccess(DataElementPreview obj) {
				onShow(obj);
			};
		});
		ex.invoke(LoadPreviewCommand.PATH, command);
	}

	private void onShow(DataElementPreview value) {
		preview.setValue(value);
	}
	
	@Override
	public void save() {
	}

	@Override
	public DataElement getValue() {
		return obj;
	}
}
