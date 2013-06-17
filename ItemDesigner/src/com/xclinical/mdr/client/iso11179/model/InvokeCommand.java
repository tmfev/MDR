package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

/**
 * Append an item to a {@link LazyList}.
 * 
 * @author ms@xclinical.com
 */
public abstract class InvokeCommand extends Command {

	public static final String PATH = "invoke";

	protected InvokeCommand() {
	}

	public static final native InvokeCommand newInstance(RemoteMethod method, Object arg) /*-{
		return {
			name : method.name,
			args : [ arg ]
		};
	}-*/;

	public static final native InvokeCommand newInstance(RemoteMethod method, Object arg1, Object arg2) /*-{
		return {
			name : method.name,
			args : [ arg1, arg2 ]
		};
	}-*/;

	public static final native InvokeCommand newInstance(RemoteMethod method, Object arg1, Object arg2, Object arg3) /*-{
		return {
			name : method.name,
			args : [ arg1, arg2, arg3 ]
		};
	}-*/;
}
