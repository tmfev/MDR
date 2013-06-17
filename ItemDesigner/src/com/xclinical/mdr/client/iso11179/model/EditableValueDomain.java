package com.xclinical.mdr.client.iso11179.model;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.util.JsList;

public final class EditableValueDomain extends EditableItem {

	protected EditableValueDomain() {
	}
	
	public native DataType getDataType() /*-{
		return this.dataType;
	}-*/;

	public native void setDataType(DataType dataType) /*-{
		this.dataType = dataType;
	}-*/;

	public native int getMaximumCharacterQuantity() /*-{
		return this.maximumCharacterQuantity | 0;
	}-*/;

	public native void setMaximumCharacterQuantity(int maximumCharacterQuantity) /*-{
		this.maximumCharacterQuantity = maximumCharacterQuantity;
	}-*/;

	public final native String getFormat() /*-{
		return this.format;
	}-*/;

	public final native void setFormat(String format) /*-{
		this.format = format;
	}-*/;	
	
	public native UnitOfMeasure getUnitOfMeasure() /*-{
		return this.unitOfMeasure;
	}-*/;

	public native void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) /*-{
		this.unitOfMeasure = unitOfMeasure;
	}-*/;

	public native JsArray<CodeListElement> getCodeList0() /*-{
		if (this.codeList == undefined)
			this.codeList = [];
		return this.codeList;
	}-*/;

	public List<CodeListElement> getCodeList() {
		return new JsList<CodeListElement>(getCodeList0());
	}
}
