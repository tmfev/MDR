package com.xclinical.mdr.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

public class ViewAsEvent extends Event<ViewAsEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();

	private Mode mode;
	
	public ViewAsEvent(Mode mode) {
		this.mode = mode;
	}
	
	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onViewChanged(mode);
	}
	
	public interface Handler extends EventHandler {

		void onViewChanged(Mode mode);
	}

	public enum Mode {
		DEFAULT(false),
		ISO11179(true),
		BROWSER(false),
		PREVIEW(true),		
		SIMPLE(true),
		SOURCE(false),
		BULK(false);
		
		private final boolean defaultable;
		
		Mode(boolean defaultable) {
			this.defaultable = defaultable;
		}
		
		public boolean isDefaultable() {
			return defaultable;
		}
	}
}
