package com.xclinical.mdr.client.iso11179.model;


public final class DataElementConcept extends Concept {

	public static final String URN = "urn:mdr:DataElementConcept";

	public static final String CONCEPTUAL_DOMAINS = "DataElementConcept.conceptualDomains";

	public static final String REPRESENTATIONS = "DataElementConcept.representations";
	
	protected DataElementConcept() {
	}

	public final LazyList getConceptualDomainList() {
		return LazyList.of(CONCEPTUAL_DOMAINS, this);
	}
	
	public final LazyList getRepresentationList() {
		return LazyList.of(REPRESENTATIONS, this);
	}
	
	public final native ObjectClass getObjectClass() /*-{
		return this.objectClass;
	}-*/;

	public final native void setObjectClass(ObjectClass objectClass) /*-{
		this.objectClass = objectClass;
	}-*/;

	public final native Characteristic getCharacteristic() /*-{
		return this.characteristic;
	}-*/;

	public final native void setCharacteristic(Characteristic characteristic) /*-{
		return this.characteristic = characteristic;
	}-*/;

	public static native <T extends Item> T newInstance() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.DataElementConcept::URN,
			representations: [],
			conceptualDomains: []
		};
	}-*/;
}
