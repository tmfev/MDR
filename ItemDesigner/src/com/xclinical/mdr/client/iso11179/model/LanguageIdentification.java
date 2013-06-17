package com.xclinical.mdr.client.iso11179.model;



public final class LanguageIdentification extends Item {

	public static final String URN = "urn:mdr:LanguageIdentification";
	
	protected LanguageIdentification() {}
	
	public final native String getLanguageIdentifier() /*-{
		return this.languageIdentifier;
	}-*/; 

	public final native void setLanguageIdentifier(String languageIdentifier) /*-{
		this.languageIdentifier = languageIdentifier;
	}-*/; 
}
