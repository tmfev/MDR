package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public final class ExportDocumentCommand extends Command {

	public static final String PATH = "exportDocument";
	
	protected ExportDocumentCommand() {
	}

	public final native Item getItem() /*-{
		return this.item;
	}-*/;
	
	public static final native ExportDocumentCommand newInstance(Item item) /*-{
		return {
			item: item
		};
	}-*/;
}
