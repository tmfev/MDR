package com.xclinical.mdr.client.iso11179.model;


public final class Context extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:Context";

	public static final String RELEVANT_DESIGNATIONS = "Context.relevantDesignations";
	
	protected Context() {
	}

	public final LazyList getRelevantDesignationList() {
		return LazyList.of(RELEVANT_DESIGNATIONS, this);
	}
	
	public static native Context newContext() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.Context::URN
		};
	}-*/;
}
