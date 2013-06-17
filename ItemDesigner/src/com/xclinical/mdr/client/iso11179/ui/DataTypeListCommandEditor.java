package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemList;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.DataTypeListCommand;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.ResultList;

public class DataTypeListCommandEditor extends Composite implements GenericEditor<Item>, Editor<DataTypeListCommand> {

	interface Binder extends UiBinder<Widget, DataTypeListCommandEditor> {		
	}
	
	interface Driver extends SimpleBeanEditorDriver<DataTypeListCommand, DataTypeListCommandEditor> {		
	}
	
	@UiField
	DataTypeListEditor elementList;

	@UiField
	@Ignore
	ItemList result;
		
	private Driver driver;
	
	public DataTypeListCommandEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}
	
	@Override
	public void open(Item obj) {
		DataTypeListCommand command = DataTypeListCommand.newInstance();
		driver.edit(command);
	}

	@Override
	public void save() {
		CommandExecutor<ResultList> executor = new CommandExecutor<ResultList>();
		executor.setOnResult(new OnSuccessHandler<ResultList>() {
			@Override
			public void onSuccess(ResultList obj) {
				elementList.setVisible(false);
				result.setVisible(true);

				result.setList(obj.getElementList());
			}
		});
		executor.invoke(DataTypeListCommand.PATH, driver.flush());		
	}	
	
	@Override
	public Item getValue() {
		return null;
	}	
}
