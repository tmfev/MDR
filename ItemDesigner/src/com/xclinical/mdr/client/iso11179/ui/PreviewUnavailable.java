package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.GenericEditor;

public class PreviewUnavailable<T> extends Composite implements GenericEditor<T> {

	interface Binder extends UiBinder<Widget, PreviewUnavailable<?>> {}

	private T obj;
	
	public PreviewUnavailable() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
	}

	@Override
	public void open(T obj) {
		this.obj = obj;
	}

	@Override
	public void save() {
	}

	@Override
	public T getValue() {
		return obj;
	}
}
