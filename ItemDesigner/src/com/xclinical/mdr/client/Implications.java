package com.xclinical.mdr.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.ui.MessageBox;
import com.xclinical.mdr.client.windows.Window;
import com.xclinical.mdr.client.windows.WindowManager;
import com.xclinical.mdr.client.windows.WindowState;
import com.xclinical.mdr.repository.Key;

public final class Implications {

	private Implications() {}
	
	public static Item requiresImplication(Key type) {
		Item item = optionalImplication(type);
		
		if (item == null) {
			MessageBox mb = new MessageBox("You must add a " + type.getSimpleName() + " implication first");
			mb.showPopup();
		}
		
		return item;
	}

	public static Item optionalImplication(Key type) {
		WindowManager wm = WindowManager.get();
		for (Window wnd : wm.findWindows(WindowState.MINIMIZED)) {
			IsWidget body = wnd.getBody();
			if (body instanceof GenericEditor<?>) {
				Item item = ((GenericEditor<Item>)body).getValue();
				Key key = item.getKey();
				if (key != null && key.getName().equals(type.getName())) {
					return item;
				}
			}
		}
		
		return null;
	}	
}
