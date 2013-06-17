package com.xclinical.mdr.client.iso11179.model;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.xclinical.mdr.client.io.Command;
import com.xclinical.mdr.client.util.JsList;

public final class UnitOfMeasureListCommand extends Command {

	public static final String PATH = "saveUnitOfMeasureList";

	protected UnitOfMeasureListCommand() {
	}

	public final native Context getParent() /*-{
		return this.parent;
	}-*/;

	public final native void setParent(Context parent) /*-{
		this.parent = parent;
	}-*/;

	public final native LanguageIdentification getLanguage() /*-{
		return this.language;
	}-*/;

	public final native void setLanguage(LanguageIdentification language) /*-{
		this.language = language;
	}-*/;

	public native JsArray<EditableItem> getElements() /*-{
		return this.elements;
	}-*/;

	public List<EditableItem> getElementList() {
		return new JsList<EditableItem>(getElements());
	}

	public static native UnitOfMeasureListCommand newInstance() /*-{
		return {
			elements : []
		};
	}-*/;
}
