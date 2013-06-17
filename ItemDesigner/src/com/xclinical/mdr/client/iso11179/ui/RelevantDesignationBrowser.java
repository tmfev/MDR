package com.xclinical.mdr.client.iso11179.ui;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.GenericEditorFactory;
import com.xclinical.mdr.client.HistoryItemProvider;
import com.xclinical.mdr.client.ItemCell;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.AddChildCommand;
import com.xclinical.mdr.client.iso11179.model.FindFirstCommand;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadListCommand;
import com.xclinical.mdr.client.iso11179.model.RemoveChildCommand;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.reflect.Reflection;
import com.xclinical.mdr.client.util.JsList;
import com.xclinical.mdr.client.windows.Clipboard;
import com.xclinical.mdr.client.windows.HasClipboardData;
import com.xclinical.mdr.client.windows.HasWindow;
import com.xclinical.mdr.client.windows.Window;

public class RelevantDesignationBrowser extends Composite implements GenericEditor<Item>, HasWindow, HasItem, HasClipboardData {

	interface Binder extends UiBinder<Widget, RelevantDesignationBrowser> {
		Binder B = GWT.create(Binder.class);
	}

	interface Style extends CssResource {
		String grow();
	}

	@UiField
	Style style;

	@UiField
	SimplePanel panel;

	CellBrowser browser;

	private Item initialValue;
	
	private Window window;

	private TreeViewModel model;

	private ParentedObject<Item> lastItem;
	
	private static final ProvidesKey<ParentedObject<Item>> KEY_PROVIDER = new ProvidesKey<ParentedObject<Item>>() {		
		@Override
		public Object getKey(ParentedObject<Item> item) {
			if (item.getObject().getId() == null) {
				return item;
			}
			else {
				return item.getObject().getId();
			}
		}
	};

	private final SingleSelectionModel<ParentedObject<Item>> selectionModel;
	
	private final ParentedObjectCell cell = new ParentedObjectCell(	
		new ItemCell(false) {
		protected void onItemClicked(Item entity) {
		};

		protected void onDeleteClicked(Item entity) {
		};
	});
	
	public RelevantDesignationBrowser() {
		selectionModel = new SingleSelectionModel<ParentedObject<Item>>(KEY_PROVIDER);
		
		initWidget(Binder.B.createAndBindUi(this));
	}

	private void onItemClicked(final ParentedObject<Item> o) {
		Item entity = o.getObject();
		
		FindFirstCommand command = FindFirstCommand.newInstance(entity.getId());
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				HistoryItemProvider.get().add(obj);
				
				lastItem = o;
				
				/*
				GenericEditor<? super Item> itemEditor = Editors.createEditor(obj, ItemDesigner.get().getDefaultMode());
				editor.setWidget(itemEditor);			
				itemEditor.open(obj);
				
				window.setTitle(initialValue.getDisplayName() + ": " + obj.getDisplayName());
				*/
			}
		});
		executor.invoke(FindFirstCommand.PATH, command);											
	}

	private void onItemRemoved(ParentedObject<Item> o) {
		RemoveChildCommand command = RemoveChildCommand.newInstance(o.getParent(), o.getObject());
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				// TODO: Refresh parent.
			}
		});
		executor.invoke(RemoveChildCommand.PATH, command);													
	}
	
	@Override
	public void setWindow(Window wnd) {
		this.window = wnd;		
	}
	
	@Override
	public void open(Item obj) {
		initialValue = obj;
		
		browser = new CellBrowser(model = new Model(), new ParentedObject<Item>(null, initialValue));
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		browser.setSize("100%", "100%");
		panel.setWidget(browser);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void save() {
	}

	@Override
	public Item getValue() {
		return getItem();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item getItem() {
		return initialValue;
	}
	
	private static class ParentedObject<T> {
		private final T parent;
		
		private final T obj;
		
		public ParentedObject(T parent, T obj) {
			this.parent = parent;
			this.obj = obj;
		}
		
		public T getParent() {
			return parent;
		}
		
		public T getObject() {
			return obj;
		}
	}

	private class ParentedObjectCell extends AbstractCell<ParentedObject<Item>> {

		private final Cell<Item> inner;
		
		public ParentedObjectCell(Cell<Item> inner) {
			super("click", "blur");
			this.inner = inner;
		}
		
		@Override
		public void render(Cell.Context context, ParentedObject<Item> value, SafeHtmlBuilder sb) {
			inner.render(context, value.getObject(), sb);
		}
		
		@Override
		public void onBrowserEvent(Cell.Context context, Element parent, ParentedObject<Item> value,
				NativeEvent event, ValueUpdater<ParentedObject<Item>> valueUpdater) {

			String eventType = event.getType();
			if ("click".equals(eventType)) {
				EventTarget eventTarget = event.getEventTarget();
				if (Element.is(eventTarget)) {
					Element target = Element.as(eventTarget);
					if ("img".equals(target.getTagName().toLowerCase())) {
						onItemRemoved(value);
					}
					else {
						onItemClicked(value);
					}
				}
			}
			else {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
			}
			
			inner.onBrowserEvent(context, parent, value.getObject(), event, null);
		}		
	}
	
	private class Model implements TreeViewModel {
		@Override
		public <N> NodeInfo<?> getNodeInfo(N value) {
			ParentedObject<Item> o = (ParentedObject<Item>)value;			
			Item item = (Item) o.getObject();
			GenericEditorFactory<Item> factory = Reflection.newInstance(item.getKey().getName());
			AbstractDataProvider<ParentedObject<Item>> p = new ChildDataProvider(item, factory.getChildren(item));
			
			return new DefaultNodeInfo<ParentedObject<Item>>(p, cell, selectionModel, null);
		}

		@Override
		public boolean isLeaf(Object value) {
			ParentedObject<Item> o = (ParentedObject<Item>)value;			
			Item item = o.getObject();
			GenericEditorFactory<Item> factory = Reflection.newInstance(item.getKey().getName());
			return factory.getChildren(item) == null;
		}
	}

	private static class ChildDataProvider extends AsyncDataProvider<ParentedObject<Item>> {

		private final Item value;

		private final LazyList list;
		
		public ChildDataProvider(Item value, LazyList list) {
			this.value = value;
			this.list = list;		
		}
		
		@Override
		protected void onRangeChanged(final HasData<ParentedObject<Item>> display) {
			if (list == null) {
				display.setRowCount(0);
			}
			else {
				final Range range = display.getVisibleRange();
				final int start = range.getStart();
				final int length = range.getLength();
				
				CommandExecutor<ResultList> ex = new CommandExecutor<ResultList>();
				ex.setOnResult(new OnSuccessHandler<ResultList>() {
					@Override
					public void onSuccess(ResultList obj) {
						JsList<? extends Item> itemList = obj.getElementList();
						onDataChanged(display, start, obj.getLength(), itemList);
					}
				});
				
				ex.invoke(LoadListCommand.PATH, LoadListCommand.newInstance(list, start, length));
			}
		}
		
		protected void onDataChanged(HasData<ParentedObject<Item>> display, int start, int size, JsList<? extends Item> items) {
			List<ParentedObject<Item>> list = new ArrayList<ParentedObject<Item>>();

			for (int i = 0; i < items.size(); i++) {
				list.add(new ParentedObject<Item>(value, items.get(i)));
			}
						
			display.setRowData(start, list);
			
			if (start == 0) {
				display.setRowCount(size, true);
			}		
		}
	}
	
	public void onDelete() {		
	}
	
	@Override
	public void onCopy(Clipboard clipboard) {
		clipboard.setData(getValue());
	}

	@Override
	public void onPaste(Clipboard clipboard) {
		CommandExecutor<Item> ex = new CommandExecutor<Item>();
		ex.setOnResult(new OnSuccessHandler<Item>() {
			@Override
			public void onSuccess(Item obj) {
			}
		});
		ex.invoke(AddChildCommand.PATH, AddChildCommand.newInstance(getValue(), (Item)clipboard.getData()));
	}
	
	@Override
	public void onRemove(Clipboard clipboard) {
		onItemRemoved(lastItem);
	}
}
