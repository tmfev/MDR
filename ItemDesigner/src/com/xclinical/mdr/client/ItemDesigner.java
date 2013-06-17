package com.xclinical.mdr.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.FindFirstCommand;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LoginInfo;
import com.xclinical.mdr.client.reflect.Reflection;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.ui.AbstractPerspective;
import com.xclinical.mdr.client.ui.Perspective;
import com.xclinical.mdr.client.ui.Platform;
import com.xclinical.mdr.client.ui.Workbench;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.client.windows.Window;
import com.xclinical.mdr.client.windows.WindowManager;
import com.xclinical.mdr.client.windows.WindowState;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ItemDesigner implements EntryPoint {

	public static final String LOGIN_PERSPECTIVE = "Login";

	public static final String MAIN_PERSPECTIVE = "Main";

	private static ItemDesigner instance;
	
	private EventBus eventBus = GWT.create(SimpleEventBus.class);
	
	private Mode defaultMode = Mode.SIMPLE;
	
	public ItemDesigner() {
		instance = this;
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		MdrResources.INSTANCE.css().ensureInjected();
		Reflection.initialize();

		Platform.initialize("Design Studio");
		
		final Perspective main = new MainPerspective();

		Perspective login = new AbstractPerspective() {
			public void attach(final Workbench wb) {
				LoginLayout layout = new LoginLayout();
				RootLayoutPanel.get().add(layout);
			}
		};

		final Workbench wb = Platform.getWorkbench();
		wb.addPerspective(LOGIN_PERSPECTIVE, login);
		wb.addPerspective(MAIN_PERSPECTIVE, main);

		eventBus.addHandler(SaveEvent.TYPE, new SaveEvent.Handler() {
			@Override
			public void onSave(SaveEvent event) {
				Window activeWindow = WindowManager.get().getActiveWindow();
				if (activeWindow != null) {
					IsWidget body = activeWindow.getBody();
					((GenericEditor<?>)body).save();
				}
			}
		});

		eventBus.addHandler(ViewAsEvent.TYPE, new ViewAsEvent.Handler() {
			@Override
			public void onViewChanged(Mode mode) {
				if (mode.isDefaultable()) {
					defaultMode = mode;
				}
				
				Window activeWindow = WindowManager.get().getActiveWindow();
				if (activeWindow != null) {
					IsWidget body = activeWindow.getBody();
					if (body instanceof GenericEditor<?>) {
						Item item = ((GenericEditor<Item>)body).getValue();
						open(item, mode, false);
					}
				}
			}
		});

		eventBus.addHandler(SessionChangedEvent.TYPE, new SessionChangedEvent.Handler() {
			@Override
			public void onSessionChanged(LoginInfo info) {
				if (info == null) {
					wb.activatePerspective(LOGIN_PERSPECTIVE);					
				}
				else {
					wb.activatePerspective(MAIN_PERSPECTIVE);
				}
			}
		});
		
		String credentials = LoginUtils.getCredentials();
		if (credentials != null) {
			LoginUtils.refresh(credentials);
		} else {
			wb.activatePerspective(LOGIN_PERSPECTIVE);
		}
	}
	
	public static ItemDesigner get() {
		return instance;
	}

	public EventBus getEventBus() {
		return eventBus;
	}
	
	public Mode getDefaultMode() {
		return defaultMode;
	}
	
	public void open(Item item, Mode mode, boolean newWindow) {
		// Check for existing editor.
		if (newWindow) {
			WindowManager mgr = WindowManager.get();
			
			for (Window wnd : mgr.findWindows(WindowState.NORMAL)) {
				IsWidget body = wnd.getBody();
				if (body instanceof HasItem) {
					Item existing = ((HasItem)body).getItem();
					if (existing.getId().equals(item.getId())) {
						wnd.activate();
						return;
					}
				}
			}
	
			for (Window wnd : mgr.findWindows(WindowState.MINIMIZED)) {
				IsWidget body = wnd.getBody();
				if (body instanceof HasItem) {
					Item existing = ((HasItem)body).getItem();
					if (existing.getId().equals(item.getId())) {
						wnd.open();
						wnd.activate();
						return;
					}
				}
			}
		}
		
		// Open new editor.
		if (mode == Mode.DEFAULT) mode = defaultMode;

		GenericEditor<? super Item> editor = Editors.createEditor(item, mode);

		open(editor.asWidget(), item.getDisplayName(), newWindow);
		editor.open(item);		
	}

	public void open(Widget body, String title, boolean newWindow) {		
		Window wnd = WindowManager.get().getActiveWindow();
		if (newWindow || wnd == null) {
			wnd = WindowManager.get().newWindow();
		}
		
		wnd.setBody(body);
		wnd.setTitle(title);

		wnd.open();
		wnd.activate();
	}
	
	public void open(String id, final boolean newWindow) {
		FindFirstCommand command = FindFirstCommand.newInstance(id);
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				HistoryItemProvider.get().add(obj);
				
				open(obj, Mode.DEFAULT, newWindow);
			}
		});
		executor.invoke(FindFirstCommand.PATH, command);						
	}

	private static class MainPerspective extends AbstractPerspective {
		MainLayout layout;

		public void attach(Workbench wb) {
			layout = new MainLayout();
			RootLayoutPanel.get().add(layout);
			
			final String hash = Location.getHash(); 
			if (hash.length() > 0) {
				ItemDesigner.get().open(hash, true);
			}
		}
	}
}
