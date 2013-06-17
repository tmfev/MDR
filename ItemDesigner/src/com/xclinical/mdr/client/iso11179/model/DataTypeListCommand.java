package com.xclinical.mdr.client.iso11179.model;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.io.Command;
import com.xclinical.mdr.client.util.JsList;


public final class DataTypeListCommand extends Command {

	public static final String PATH = "saveDataTypeList";
	
	protected DataTypeListCommand() {
	}

	public native JsArray<DataType> getElements() /*-{
		return this.elements;
	}-*/;

	public List<DataType> getElementList() {
		return new JsList<DataType>(getElements());
	}
	
	public static native DataTypeListCommand newInstance() /*-{
		return {
			elements : [] 
		};
	}-*/;	
}
