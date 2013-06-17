package com.xclinical.mdr.client.iso11179.model;

public final class RelationRole extends Concept {

	public static final String URN = "urn:mdr:RelationRole";

	public static final String LINK_ENDS = "RelationRole.linkEnds";
	
	protected RelationRole() {
	}

	public native Relation getSource() /*-{
		return this.source;
	}-*/;

	public LazyList getLinkEndList() {
		return LazyList.of(LINK_ENDS, this);
	}
}
