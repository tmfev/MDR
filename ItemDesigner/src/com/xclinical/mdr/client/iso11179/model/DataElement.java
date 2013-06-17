package com.xclinical.mdr.client.iso11179.model;


public final class DataElement extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:DataElement";

	protected DataElement() {
	}

	public final native ValueDomain getDomain() /*-{
		return this.domain;
	}-*/;

	public final native void setDomain(ValueDomain domain) /*-{
		this.domain = domain;
	}-*/;

	public final native DataElementConcept getMeaning() /*-{
		return this.meaning;
	}-*/;

	public final native void setMeaning(DataElementConcept meaning) /*-{
		this.meaning = meaning;
	}-*/;

	public final native int getPrecision() /*-{
		return this.precision | 0;
	}-*/;

	public final native void setPrecision(int precision) /*-{
		this.precision = precision;
	}-*/;
	
	public static native <T extends Item> T newInstance() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.DataElement::URN
		};
	}-*/;

}
