package com.xclinical.mdr.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

public class SaveEvent extends Event<SaveEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();

	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	
	@Override
	protected void dispatch(Handler handler) {
		handler.onSave(this);
	}
	
	public interface Handler extends EventHandler {

		void onSave(SaveEvent event);
	}
}
