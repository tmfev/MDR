package com.xclinical.mdr.client;

import com.google.gwt.user.client.ui.IsWidget;

public interface GenericEditor<T> extends IsWidget {

	void open(T obj);
	
	void save();
	
	T getValue();
}
