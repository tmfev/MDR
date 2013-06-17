package com.xclinical.mdr.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;
import com.xclinical.mdr.client.iso11179.model.LoginInfo;

public class SessionChangedEvent extends Event<SessionChangedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();

	private LoginInfo info;
			
	public SessionChangedEvent(LoginInfo info) {
		this.info = info;
	}
	
	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSessionChanged(info);
	}
	
	public interface Handler extends EventHandler {
		void onSessionChanged(LoginInfo info);
	}
}
