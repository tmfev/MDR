package com.xclinical.mdr.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.windows.Window;
import com.xclinical.mdr.client.windows.WindowManager;

public class InfoPanel extends Composite {

	interface Binder extends UiBinder<Widget, InfoPanel> {
	}

	@UiField
	Label version;
	
	@UiField
	TextArea additionalInfo;
	
	public InfoPanel() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		
		version.setText(MdrResources.INSTANCE.version().getText());
				
		StringBuilder sb = new StringBuilder();
		
		append(sb, "Date", new Date().toString());		
		append(sb, "Version: ", MdrResources.INSTANCE.version().getText());
		append(sb, "Browser Code Name", Navigator.getAppCodeName());
		append(sb, "Browser Name", Navigator.getAppName());
		append(sb, "Browser Version", Navigator.getAppVersion());
		append(sb, "Cookies Enabled", Navigator.isCookieEnabled() ? "yes" : "no");
		append(sb, "Platform", Navigator.getPlatform());
		append(sb, "User Agent", Navigator.getUserAgent());

		WindowManager mgr = WindowManager.get();
		Window activeWindow = mgr.getActiveWindow();
		if (activeWindow != null) {
			IsWidget body = activeWindow.getBody();
			if (body instanceof HasItem) {
				Item existing = ((HasItem)body).getItem();
				append(sb, "Active Item ID", existing.getId());
				append(sb, "Active Item", new JSONObject(existing).toString());
			}			
		}
		else {
			sb.append("No active window\n");
		}

		additionalInfo.setText(sb.toString());
	}
	
	private static void append(StringBuilder sb, String name, String value) {
		sb.append(name);
		sb.append(": ");
		sb.append(value);
		sb.append("\n");		
	}
}
