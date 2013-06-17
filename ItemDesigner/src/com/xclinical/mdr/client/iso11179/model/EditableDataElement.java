package com.xclinical.mdr.client.iso11179.model;

public final class EditableDataElement extends EditableItem {

	protected EditableDataElement() {
	}

	public native ValueDomain getDomain() /*-{
		return this.domain;
	}-*/;

	public native void setDomain(ValueDomain domain) /*-{
		this.domain = domain;
	}-*/;

	public static native EditableDataElement newInstance() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.DataElement::URN
		};
	}-*/;

}
