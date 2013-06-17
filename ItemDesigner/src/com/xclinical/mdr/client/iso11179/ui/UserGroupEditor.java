package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.AbstractGenericEditorFactory;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.LazyItemList;
import com.xclinical.mdr.client.iso11179.model.EditableUserGroup;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.UserGroup;
import com.xclinical.mdr.client.reflect.Instantiable;

public class UserGroupEditor extends EditableItemEditor<EditableUserGroup, UserGroup> {

	interface Binder extends UiBinder<Widget, UserGroupEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<EditableUserGroup, UserGroupEditor> {
	}

	@UiField
	LazyItemList memberList;
	
	private Driver driver;

	public UserGroupEditor() {
		super(SaveEditableItemCommand.<EditableUserGroup>newInstance(EditableUserGroup.newInstance()), SaveEditableItemCommand.PATH);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		driver = GWT.create(Driver.class);
		driver.initialize(this);
	}

	@Override
	protected void onEdit(EditableUserGroup obj) {
		driver.edit(obj);
	}
	
	@Override
	protected EditableUserGroup flush() {
		return driver.flush();
	}
	
	@Instantiable(UserGroup.URN)
	public static class Factory extends AbstractGenericEditorFactory<UserGroup> {
		@Override
		public GenericEditor<UserGroup> createISO11179() {
			return new UserGroupEditor();
		}
		
		@Override
		protected GenericEditor<? super UserGroup> createSimple() {
			return new UserGroupEditor();
		}

		@Override
		protected GenericEditor<? super UserGroup> createPreview() {
			return new UserGroupEditor();
		}
		
		@Override
		public LazyList getChildren(UserGroup obj) {
			return LazyList.emptyList();
		}
	}	
}
