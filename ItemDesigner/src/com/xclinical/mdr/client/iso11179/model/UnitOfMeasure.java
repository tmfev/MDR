package com.xclinical.mdr.client.iso11179.model;


public final class UnitOfMeasure extends Concept {

	public static final String URN = "urn:mdr:UnitOfMeasure";

	public static final String DIMENSIONALITIES = "UnitOfMeasure.dimensionalities";
	
	protected UnitOfMeasure() {
	}

	public final LazyList getDimensionalityList() {
		return LazyList.of(DIMENSIONALITIES, this);
	}
}
