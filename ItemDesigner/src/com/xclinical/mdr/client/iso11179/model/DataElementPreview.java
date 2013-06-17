package com.xclinical.mdr.client.iso11179.model;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.util.JsList;

public final class DataElementPreview extends JavaScriptObject {

	protected DataElementPreview() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getDesignationSign() /*-{
		return this.designationSign;
	}-*/;

	public final native String getDefinitionText() /*-{
		return this.definitionText;
	}-*/;

	public final native String getUnitOfMeasure() /*-{
		return this.unitOfMeasure;
	}-*/;

	public final native String getFormat() /*-{
		return this.format;
	}-*/;
	
	public final native int getMaximumCharacterQuantity() /*-{
		return this.maximumCharacterQuantity || 0;
	}-*/;
	
	public final native ValueDomain getValueDomain() /*-{
		return this.valueDomain;
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
