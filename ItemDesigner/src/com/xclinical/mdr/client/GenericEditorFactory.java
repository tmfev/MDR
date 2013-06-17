package com.xclinical.mdr.client;

import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.iso11179.model.LazyList;

public interface GenericEditorFactory<T> {

	GenericEditor<? super T> createEditor(Mode mode);
	
	LazyList getChildren(T obj);
}
