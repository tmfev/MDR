package com.xclinical.mdr.client.iso11179.model;


public final class LinkEnd extends AssertionEnd {

	public static final String URN = "urn:mdr:LinkEnd";

	public static final String END_ROLES = "LinkEnd.endRoles";
	
	protected LinkEnd() {
	}

	public final LazyList getEndRoleList() {
		return LazyList.of(END_ROLES, this);
	}
}
