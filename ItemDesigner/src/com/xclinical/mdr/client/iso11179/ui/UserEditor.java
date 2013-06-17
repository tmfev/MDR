package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.iso11179.model.EditableItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.User;
import com.xclinical.mdr.client.reflect.Instantiable;

public class UserEditor extends EditableItemEditor<EditableItem, User> {

	interface Binder extends UiBinder<Widget, UserEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableItem, UserEditor> {
	}

	@UiField()
	ItemLink<Item> item;
	
	private Driver driver;

	public UserEditor() {
		super(SaveEditableItemCommand.newInstance(EditableItem.newInstance(User.URN)), SaveEditableItemCommand.PATH);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}

	@Override
	protected void onEdit(EditableItem obj) {
		driver.edit(obj);
	}
	
	@Override
	protected EditableItem flush() {
		return driver.flush();
	}
	
	@Instantiable(User.URN)
	public static class Factory extends AbstractGenericEditorFactory<User> {
		@Override
		public GenericEditor<User> createISO11179() {
			return new UserEditor();
		}
		
		@Override
		protected GenericEditor<? super User> createSimple() {
			return new UserEditor();
		}

		@Override
		protected GenericEditor<? super User> createPreview() {
			return new UserEditor();
		}
		
		@Override
		public LazyList getChildren(User obj) {
			return LazyList.emptyList();
		}
	}
	
}
