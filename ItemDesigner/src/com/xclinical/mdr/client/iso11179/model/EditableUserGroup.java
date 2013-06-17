package com.xclinical.mdr.client.iso11179.model;


public class EditableUserGroup extends EditableItem {

	protected EditableUserGroup() {
	}

	public final LazyList getMemberList() {
		return LazyList.of(User.ALL);
	}
	
	public static EditableUserGroup newInstance() {
		return (EditableUserGroup) newInstance(UserGroup.URN);
	}
}
