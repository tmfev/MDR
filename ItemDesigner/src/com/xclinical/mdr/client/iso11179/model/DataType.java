package com.xclinical.mdr.client.iso11179.model;

public final class DataType extends Item {

	public static final String URN = "urn:mdr:DataType";

	protected DataType() {
	}

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	public final native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	public final native String getSchemeReference() /*-{
		return this.schemeReference;
	}-*/;

	public final native void setSchemeReference(String schemeReference) /*-{
		this.schemeReference = schemeReference;
	}-*/;

	public static native DataType newInstance(String name, String description, String schemeReference) /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.DataType::URN,
			name : name,
			description : description,
			schemeReference : schemeReference
		};
	}-*/;

	public static native DataType newInstance() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.DataType::URN
		};
	}-*/;
}
