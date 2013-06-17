package com.xclinical.mdr.client.iso11179.model;



public final class ConceptSystem extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:ConceptSystem";

	public static final String INCLUDED_ASSERTIONS = "ConceptSystem.includedAssertions";

	public static final String MEMBERS = "ConceptSystem.members";
	
	protected ConceptSystem() {}
	
	public final LazyList getIncludedAssertionList() {
		return LazyList.of(INCLUDED_ASSERTIONS, this);
	}

	public final LazyList getMemberList() {
		return LazyList.of(MEMBERS, this);
	}
}
