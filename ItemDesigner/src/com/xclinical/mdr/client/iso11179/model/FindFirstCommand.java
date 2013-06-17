package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Finds the first {@link Item} with a specific id.
 * 
 * @author ms@xclinical.com
 */
public abstract class FindFirstCommand extends Command {

	public static final int MAX_HISTORY = 100;
		
	public static final String PATH = "findFirst";
	
	protected FindFirstCommand() {
	}

	public native final String getId() /*-{
		return this.id;
	}-*/;
	
	public static final native FindFirstCommand newInstance(String id) /*-{
		return {
			id : id
		};
	}-*/;
}
