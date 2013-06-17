package com.xclinical.mdr.client.iso11179.model;




public final class Namespace extends Item {

	public static final String URN = "urn:mdr:Namespace";

	protected Namespace() {}
	
	public final native String getNamespaceSchemeReference() /*-{
		return this.namespaceSchemeReference;
	}-*/; 

	public final native void setNamespaceSchemeReference(String namespaceSchemeReference) /*-{
		this.namespaceSchemeReference = namespaceSchemeReference;
	}-*/; 
}

