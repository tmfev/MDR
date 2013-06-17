package com.xclinical.mdr.client.iso11179.model;



public final class Link extends Assertion {

	public static final String URN = "urn:mdr:Link";

	protected Link() {
	}

	public final native Relation getRelation() /*-{
		return this.relation;
	}-*/;
	
}
