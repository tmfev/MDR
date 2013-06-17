package com.xclinical.mdr.client.iso11179.model;

public abstract class ConceptualDomain extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:ConceptualDomain";

	public static final String USAGES = "ConceptualDomain.usages";

	public static final String REPRESENTATIONS = "ConceptualDomain.representations";

	public static final String MEMBERS = "ConceptualDomain.members";

	protected ConceptualDomain() {
	}

	public final LazyList getUsageList() {
		return LazyList.of(USAGES, this);
	}

	public final LazyList getRepresentationList() {
		return LazyList.of(REPRESENTATIONS, this);
	}

	public final LazyList getMemberList() {
		return LazyList.of(MEMBERS, this);
	}

	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	public final native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	public static native ConceptualDomain newConceptualDomain() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.ConceptualDomain::URN
		};
	}-*/;

}
