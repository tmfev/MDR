package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.ContextPreview;
import com.xclinical.mdr.client.iso11179.model.LoadPreviewCommand;

public class ContextPreviewEditor extends Composite implements GenericEditor<Context> {

	interface Binder extends UiBinder<Widget, ContextPreviewEditor> {}

	@UiField
	ContextPreviewWidget preview;
	
	@UiField
	Image spinner;
	
	private Context obj;
	
	public ContextPreviewEditor() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
	}

	@Override
	public void open(Context obj) {
		this.obj = obj;

		LoadPreviewCommand command = LoadPreviewCommand.newInstance(obj);
		CommandExecutor<ContextPreview> ex = new CommandExecutor<ContextPreview>();
		ex.setOnResult(new OnSuccessHandler<ContextPreview>() {
			public void onSuccess(ContextPreview obj) {
				preview.setValue(obj);
				spinner.setVisible(false);
			};
		});
		ex.invoke(LoadPreviewCommand.PATH, command);
	}

	@Override
	public void save() {
	}

	@Override
	public Context getValue() {
		return obj;
	}
}
