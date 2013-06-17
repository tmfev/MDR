package com.xclinical.mdr.client.iso11179.model;

public class AssertionEnd extends Item {

	public static final String URN = "urn:mdr:AssertionEnd";

	protected AssertionEnd() {
	}

	public final native Concept getEnd() /*-{
		return this.end;
	}-*/;

	public final native Assertion getAssertion() /*-{
		return this.assertion;
	}-*/;

}
