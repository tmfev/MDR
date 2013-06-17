package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.util.JsList;

/**
 * Contains a window from a larger list, for partial updates.
 * 
 * @author ms@xclinical.com
 */
public final class ResultList extends Item {

	public static final String URN = "urn:mdr:ResultList";

	public static final String ELEMENTS = "elements";
	
	public static final String START = "start";

	public static final String LENGTH = "length";

	protected ResultList() {
	}

	public final native JsArray<Item> getElements() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.ResultList::ELEMENTS];
	}-*/;

	public final native int getStart() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.ResultList::START];
	}-*/;

	public final native int getLength() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.ResultList::LENGTH];
	}-*/;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final JsList<? extends Item> getElementList() {
		return new JsList(getElements());
	}
	
}
