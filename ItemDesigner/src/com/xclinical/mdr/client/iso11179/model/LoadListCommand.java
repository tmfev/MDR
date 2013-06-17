package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Loads the items of a specific list from the server.
 * 
 * @author ms@xclinical.com
 */
public abstract class LoadListCommand extends Command {

	public static final String PATH = "loadList";
	
	protected LoadListCommand() {
	}

	public static final native LoadListCommand newInstance(LazyList list, int start, int length) /*-{
		return {
			query: list.query,
			name : list.name,
			args : list.args,
			start : start,
			length: length
		};
	}-*/;
}
