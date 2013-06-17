package com.xclinical.mdr.client.iso11179.model;


public class ValueDomain extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:ValueDomain";

	public static final String MEMBERS = "ValueDomain.members";

	protected ValueDomain() {
	}

	public final LazyList getMemberList() {
		return LazyList.of(MEMBERS, this);
	}
	
	public final native DataType getDataType() /*-{
		return this.dataType;
	}-*/;

	public final native void setDataType(DataType dataType) /*-{
		this.dataType = dataType;
	}-*/;

	public final native String getFormat() /*-{
		return this.format;
	}-*/;

	public final native void setFormat(String format) /*-{
		this.format = format;
	}-*/;	
	
	public final native int getMaximumCharacterQuantity() /*-{
		return this.maximumCharacterQuantity | 0;
	}-*/;

	public final native void setMaximumCharacterQuantity(int maximumCharacterQuantity) /*-{
		this.maximumCharacterQuantity = maximumCharacterQuantity;
	}-*/;

	public final native UnitOfMeasure getUnitOfMeasure() /*-{
		return this.unitOfMeasure;
	}-*/;

	public final native void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) /*-{
		this.unitOfMeasure = unitOfMeasure;
	}-*/;

	public final native ConceptualDomain getMeaning() /*-{
		return this.meaning;
	}-*/;

	public final native void setMeaning(ConceptualDomain meaning) /*-{
		this.meaning = meaning;
	}-*/;

	public static native ValueDomain newValueDomain() /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.ValueDomain::URN
		};
	}-*/;
}
