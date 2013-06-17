package com.xclinical.mdr.client.iso11179.model;


public final class ValueMeaning extends Concept {

	public static final String URN = "urn:mdr:ValueMeaning";

	public static final String REPRESENTATIONS = "ValueMeaning.representations";
	
	protected ValueMeaning() {
	}

	public final LazyList getRepresentationList() {
		return LazyList.of(REPRESENTATIONS, this);
	}
}
