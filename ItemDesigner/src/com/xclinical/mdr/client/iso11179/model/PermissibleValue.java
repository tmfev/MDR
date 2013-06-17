package com.xclinical.mdr.client.iso11179.model;


public final class PermissibleValue extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:PermissibleValue";

	protected PermissibleValue() {
	}

	public final native String getPermittedValue() /*-{
		return this.permittedValue;
	}-*/;

	public final native void setPermittedValue(String permittedValue) /*-{
		this.permittedValue = permittedValue;
	}-*/;

	public final native ValueMeaning getMeaning() /*-{
		return this.meaning;
	}-*/;

	public final native void setMeaning(ValueMeaning meaning) /*-{
		this.meaning = meaning;
	}-*/;

	public static native PermissibleValue newPermissibleValue(String permittedValue, ValueMeaning meaning) /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.PermissibleValue::URN,
			permittedValue : permittedValue,
			meaning : meaning
		};
	}-*/;
}
