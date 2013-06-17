package com.xclinical.mdr.client.iso11179.model;

public class Concept extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:Concept";
	
	public static final String ASSERTIONS = "Concept.assertions";

	public static final String RELATION_ROLES = "Concept.relationRoles";

	public static final String RELATED = "Concept.related";
	
	protected Concept() {}
	
	public final LazyList getAssertionList() {
		return LazyList.of(ASSERTIONS, this);
	}

	public final native ConceptSystem getIncluding() /*-{
		return this.including;
	}-*/;

	public final native void setIncluding(ConceptSystem including) /*-{
		this.including = including;
	}-*/;
}