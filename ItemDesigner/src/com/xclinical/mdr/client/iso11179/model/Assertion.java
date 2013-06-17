package com.xclinical.mdr.client.iso11179.model;


public class Assertion extends Item {

	public static final String URN = "urn:mdr:Assertion";

	public static final String TERMS = "Assertion.terms";
	
	protected Assertion() {
	}

	public final LazyList getTermList() {
		return LazyList.of(TERMS, this);
	}
	
	public final native ConceptSystem getAssertor() /*-{
		return this.assertor;
	}-*/;

	public final native void setAssertor(ConceptSystem assertor) /*-{
		this.assertor = assertor;
	}-*/;

	public final native String getFormula() /*-{
		return this.formula;
	}-*/;

	public final native void setFormula(String formula) /*-{
		this.formula = formula;
	}-*/;
}
