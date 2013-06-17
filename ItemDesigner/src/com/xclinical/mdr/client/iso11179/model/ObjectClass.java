package com.xclinical.mdr.client.iso11179.model;



public final class ObjectClass extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:ObjectClass";

	public static final String CONCEPTS = "ObjectClass.concepts";
	
	protected ObjectClass() {}
	
	public final LazyList getConceptList() {
		return LazyList.of(CONCEPTS, this);
	}
}
