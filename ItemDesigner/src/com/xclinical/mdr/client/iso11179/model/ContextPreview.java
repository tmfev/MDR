package com.xclinical.mdr.client.iso11179.model;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.util.JsList;

public final class ContextPreview extends JavaScriptObject {

	protected ContextPreview() {
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

	public native JsArray<ContextPreview> getChildren0() /*-{
		if (this.children == undefined)
			this.children = [];
		return this.children;
	}-*/;

	public List<ContextPreview> getChildren() {
		return new JsList<ContextPreview>(getChildren0());
	}

	public native JsArray<DataElementPreview> getDataElements0() /*-{
		if (this.dataElements == undefined)
			this.dataElements = [];
		return this.dataElements;
	}-*/;

	public List<DataElementPreview> getDataElements() {
		return new JsList<DataElementPreview>(getDataElements0());
	}
}
