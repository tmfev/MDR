package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A definition of a list that can be loaded lazily from the server.
 * 
 * @author ms@xclinical.com
 */
public final class LazyList extends JavaScriptObject {

	protected LazyList() {
	}

	public final native boolean isEmpty() /*-{
		return this.name == null;
	}-*/;

	public static final LazyList emptyList() {
		return LazyList.newList(null);
	}
	
	public static final LazyList of(String name, Item item) {
		if (item.isNew()) {
			return emptyList();
		} else {
			return newList(name, Item.ref(item));
		}
	}

	public static final LazyList of(String name, Item item1, Item item2) {
		return newList(name, Item.ref(item1), Item.ref(item2));
	}
	
	public static final LazyList of(String name) {
		return newList(name);
	}

	public final native LazyList setAppender(RemoteMethod method) /*-{
		this.appender = method;
		return this;
	}-*/;

	public final native RemoteMethod getAppender() /*-{
		return this.appender;
	}-*/;
	
	public final native LazyList setRemover(RemoteMethod method) /*-{
		this.remover = method;
		return this;
	}-*/;
	
	public final native RemoteMethod getRemover() /*-{
		return this.remover;
	}-*/;
	
	private static final native LazyList newList(String name) /*-{
		return {
			name : name,
			args : []
		};
	}-*/;

	private static final native LazyList newList(String name, Object arg) /*-{
		return {
			name : name,
			args : [ arg ]
		};
	}-*/;

	private static final native LazyList newList(String name, Object arg1, Object arg2) /*-{
		return {
			name : name,
			args : [ arg1, arg2 ]
		};
	}-*/;
	
	/**
	 * Create a lazy list from a literal JPA query.
	 * 
	 * @param query is the query string.
	 * @return the lazy list.
	 */
	public static final native LazyList query(String query) /*-{
		return {
			query : query
		};
	}-*/;

	public final native JavaScriptObject getArg(int index) /*-{
		return this.args[index];
	}-*/;
	
}
