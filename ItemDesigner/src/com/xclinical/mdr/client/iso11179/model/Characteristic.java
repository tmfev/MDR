package com.xclinical.mdr.client.iso11179.model;



public final class Characteristic extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:Characteristic";

	public static final String CONCEPTS = "Characteristic.concepts";
	
	protected Characteristic() {}
	
	public final LazyList getConceptList() {
		return LazyList.of(CONCEPTS, this);
	}
}
