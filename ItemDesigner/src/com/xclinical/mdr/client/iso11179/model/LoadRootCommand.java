package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Loads the entry point of the repository.
 * 
 * @author ms@xclinical.com
 */
public abstract class LoadRootCommand extends Command {

	public static final String PATH = "loadRoot";

	protected LoadRootCommand() {}
	
	public static native LoadRootCommand newInstance() /*-{
		return {};
	}-*/;
}
