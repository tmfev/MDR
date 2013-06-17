package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class PinnableWidget extends Composite {

	interface MyUiBindder extends UiBinder<Widget, PinnableWidget> {}
	private static MyUiBindder uiBinder = GWT.create(MyUiBindder.class);
	
	@UiField
	Label label;
	
	@UiField
	ToggleButton pinned;
	
	public PinnableWidget(String name) {
		initWidget(uiBinder.createAndBindUi(this));
		label.setText(name);
	}
	
	public boolean isPinned() {
		return pinned.isDown();
	}
	
	public void showPin(boolean visible) {
		pinned.setVisible(visible);
	}
}
