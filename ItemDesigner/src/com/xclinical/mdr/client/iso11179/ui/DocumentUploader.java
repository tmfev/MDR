package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.xclinical.mdr.client.ItemList;
import com.xclinical.mdr.client.io.WebClient;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.ui.SwitchPanel;
import com.xclinical.mdr.client.util.LoginUtils;

public class DocumentUploader extends Composite {

	public interface Binder extends UiBinder<Widget, DocumentUploader> {		
	}

	private ListDataProvider<Item> data;
	
	@UiField
	ItemList documents;
	
	@UiField
	SwitchPanel switcher;
	
	@UiField
	Widget uploadPanel;

	@UiField
	Widget waitPanel;

	@UiField
	FormPanel form;

	@UiField
	Hidden session;

	@UiField
	HTML message;
	
	public DocumentUploader() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
		session.setValue(LoginUtils.currentSession());
		switcher.setVisibleWidget(uploadPanel);
		
		data = new ListDataProvider<Item>();		
		data.addDataDisplay(documents.getCellList());
		documents.setNewWindow(true);
		
		form.setAction(WebClient.makeServiceUrl("document"));		
	}

	@UiHandler("upload")
	void onClick(ClickEvent event) {
		form.submit();
	}
	
	@UiHandler("form")
	void onSubmit(SubmitEvent event) {
		switcher.setVisibleWidget(waitPanel);
	}

	@UiHandler("form")
	public void onSubmitComplete(SubmitCompleteEvent event) {
		switcher.setVisibleWidget(uploadPanel);
		String message = event.getResults();
		
		RegExp re = RegExp.compile("<pre.*>(.*)");
		MatchResult match = re.exec(message);
		
		if (match != null) {
			String json = match.getGroup(1);
			GWT.log("JSON Response:" + json);
			Item item = JsonUtils.unsafeEval(json);
			data.getList().add(item);
		}
		else {
			this.message.setText("The upload completed: " + message);
		}
	}	
}
