package com.xclinical.mdr.client.iso11179.model;


public final class Definition extends Item {

	public static final String URN = "urn:mdr:Definition";

	protected Definition() {
	}

	public final native String getText() /*-{
		return this.text;
	}-*/;

	public final native void setText(String text) /*-{
		this.text = text;
	}-*/;

	public final native LanguageIdentification getLanguage() /*-{
		return this.language;
	}-*/;

	public final native void setLanguage(LanguageIdentification language) /*-{
		this.language = language;
	}-*/;

	public final native String getSourceReference() /*-{
		return this.sourceReference;
	}-*/;

	public final native void setSourceReference(String sourceReference) /*-{
		this.sourceReference = sourceReference;
	}-*/;

	public final native Item getDefinedItem() /*-{
		return this.item;
	}-*/;

	public final native void setDefinedItem(Item item) /*-{
		this.item = item;
	}-*/;

	public static final native Definition newDefinition(String text, LanguageIdentification lang, Context context) /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.Definition::URN,
			text : text,
			language : lang,
			scopes : [ context ]
		};
	}-*/;

}
