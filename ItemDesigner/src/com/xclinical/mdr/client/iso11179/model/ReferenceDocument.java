package com.xclinical.mdr.client.iso11179.model;



public final class ReferenceDocument extends AbstractDesingatableItem {

	public static final String URN = "urn:mdr:ReferenceDocument";

	public static final String IMPORT = "importWith";
	
	protected ReferenceDocument() {}
	
	public final native String getIdentifier() /*-{
		return this.identifier;
	}-*/; 

	public final native void setIdentifier(String identifier) /*-{
		 this.identifier = identifier;
	}-*/; 
	
	public final native String getTypeDescription() /*-{
		return this.typeDescription;
	}-*/; 

	public final native void setTypeDescription(String typeDescription) /*-{
		this.typeDescription = typeDescription;
	}-*/; 
	
	public final native LanguageIdentification getLanguage() /*-{
		return this.language;
	}-*/; 

	public final native void setLanguage(LanguageIdentification language) /*-{
		this.language = language;
	}-*/; 
	
	public final native String getTitle() /*-{
		return this.title;
	}-*/; 

	public final native void setTitle(String title) /*-{
		this.title = title;
	}-*/; 
	
	public final native String getUri() /*-{
		return this.uri;
	}-*/; 

	public final native void setUri(String uri) /*-{
		this.uri = uri;
	}-*/; 
}
