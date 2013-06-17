package com.xclinical.mdr.client.iso11179.model;


public final class Designation extends Item {

	public static final String URN = "urn:mdr:Designation";

	protected Designation() {
	}

	public final native String getSign() /*-{
		return this.sign;
	}-*/;

	public final native void setSign(String sign) /*-{
		this.sign = sign;
	}-*/;

	public final native LanguageIdentification getLanguage() /*-{
		return this.language || null;
	}-*/;

	public final native void setLanguage(LanguageIdentification language) /*-{
		this.language = language;
	}-*/;

	public final native Item getDesignatedItem() /*-{
		return this.item;
	}-*/;

	public final native void setDesignatedItem(Item item) /*-{
		this.item = item;
	}-*/;

	public static final native Designation newDesignation(String sign, LanguageIdentification lang, Context context) /*-{
		return {
			id : @com.xclinical.mdr.client.iso11179.model.Designation::URN,
			sign : sign,
			language : lang,
			scopes : [context]
		};
	}-*/;
}
