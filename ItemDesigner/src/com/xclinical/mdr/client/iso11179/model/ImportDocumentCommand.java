package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public final class ImportDocumentCommand extends Command {

	public static final String PATH = "importDocument";
	
	protected ImportDocumentCommand() {
	}

	public final native Item getFormat() /*-{
		return this.format;
	}-*/;

	public final native Item getDocument() /*-{
		return this.document;
	}-*/;
	
	public static final native ImportDocumentCommand newInstance(Item document, Item format) /*-{
		return {
			document: document,
			format: format
		};
	}-*/;
}
