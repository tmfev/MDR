package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemDesigner;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.DataType;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.SimpleDataTypeCommand;

public class SimpleDataTypeEditor extends Composite implements
		GenericEditor<DataType>, Editor<DataType>, HasItem {

	interface Binder extends UiBinder<Widget, SimpleDataTypeEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<DataType, SimpleDataTypeEditor> {
	}

	@UiField
	RatingPanel rating;

	@UiField
	TextBox name;

	@UiField
	TextBox description;

	@UiField
	TextBox schemeReference;
	
	private Driver driver;

	private DataType item;
	
	public SimpleDataTypeEditor() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}

	@Override
	public void open(DataType dataType) {
		item = dataType;
		driver.edit(dataType);
	}

	@Override
	public void save() {
		SimpleDataTypeCommand command = SimpleDataTypeCommand.newInstance(getValue());
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {
			public void onSuccess(Item obj) {
				ItemDesigner.get().open(obj, Mode.DEFAULT, false);
			};
		});
		executor.invoke(SimpleDataTypeCommand.PATH, command);
	}
	
	@Override
	public DataType getValue() {
		return driver.flush();
	}
	
	@Override
	public Item getItem() {
		return item;
	}
}
