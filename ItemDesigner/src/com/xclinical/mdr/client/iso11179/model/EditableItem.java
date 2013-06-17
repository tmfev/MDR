package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;

public class EditableItem extends JavaScriptObject {

	protected EditableItem() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native void setId(String id) /*-{
		this.id = id;
	}-*/;

	public final native Rating getRating() /*-{
		return this.rating;
	}-*/;

	public final native void setRating(Rating rating) /*-{
		this.rating = rating;
	}-*/;

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

	public final native Designation getDesignation() /*-{
		return this.designation;
	}-*/;

	public final native void setDesignation(Designation designation) /*-{
		this.designation = designation;
	}-*/;

	public final native Definition getDefinition() /*-{
		return this.definition;
	}-*/;

	public final native void setDefinition(Definition definition) /*-{
		this.definition = definition;
	}-*/;

	public final native String getDesignationSign() /*-{
		return this.designationSign;
	}-*/;

	public final native void setDesignationSign(String designationSign) /*-{
		this.designationSign = designationSign;
	}-*/;

	public final native String getDefinitionText() /*-{
		return this.definitionText;
	}-*/;

	public final native void setDefinitionText(String definitionText) /*-{
		this.definitionText = definitionText;
	}-*/;

	public static native EditableItem newInstance(String id) /*-{
		return {
			id : id
		};
	}-*/;

	public final Item getItem() {
		Item i = Item.newInstance(getId());
		i.setDisplayName(getDesignationSign());
		return i;
	}
}
