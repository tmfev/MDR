package com.xclinical.mdr.client.iso11179.model;



public final class Dimensionality extends Concept {

	public static final String URN = "urn:mdr:Dimensionality";

	public static final String APPLICABLE_UNITS = "Dimensionality.applicableUnits";
	
	protected Dimensionality() {}
	
	public final LazyList getApplicableUnitList() {
		return LazyList.of(APPLICABLE_UNITS, this);
	}	
}
