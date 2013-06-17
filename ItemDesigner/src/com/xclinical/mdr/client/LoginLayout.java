package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.xclinical.mdr.client.iso11179.model.LoginInfo;
import com.xclinical.mdr.client.ui.Platform;
import com.xclinical.mdr.client.ui.SignInWidget;
import com.xclinical.mdr.client.ui.SignUpWidget;

public class LoginLayout extends Composite {

	interface Binder extends UiBinder<Widget, LoginLayout> {}

	@UiField
	SignInWidget signIn;

	@UiField
	SignUpWidget signUp;

	private HandlerRegistration registration;
	
	public LoginLayout() {		
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));		
	}
	
	@Override
	protected void onAttach() {
		registration = ItemDesigner.get().getEventBus().addHandler(SessionChangedEvent.TYPE, new SessionChangedEvent.Handler() {
			@Override
			public void onSessionChanged(LoginInfo info) {
				if (info != null) {
					Platform.getWorkbench().activatePerspective(ItemDesigner.MAIN_PERSPECTIVE);
				}
			}
		});
		super.onAttach();
	}
	
	@Override
	protected void onDetach() {
		registration.removeHandler();
		super.onDetach();
	}
}
