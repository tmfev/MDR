package com.xclinical.mdr.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.InvokeCommand;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.RemoteMethod;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.res.StandardImageBundle;

/**
 * Shows items in a list.
 * The items in the list come from a {@link LazyList}.
 * 
 * @author ms@xclinical.com
 */
public class LazyItemList extends AbstractPager implements IsEditor<Editor<LazyList>>,
		TakesValue<LazyList>, DropTarget {

	interface Binder extends UiBinder<Widget, LazyItemList> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private LazyListDataProvider provider;

	/**
	 * Customized resources for this list.
	 */
	public interface Resources extends CellList.Resources {
		@Source("EntityList.css")
		CellList.Style cellListStyle();
	}

	private static final Resources DEFAULT_RESOURCES = GWT.create(Resources.class);

	@UiField
	protected ScrollPanel scrollable;

	/**
	 * The default increment size.
	 */
	private static final int DEFAULT_INCREMENT = 20;

	/**
	 * The increment size.
	 */
	private int incrementSize = DEFAULT_INCREMENT;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	private ItemCell cell;
	
	protected final CellList<Item> cellList;

	private boolean newWindow;

	private MultiSelectionModel<Item> selectionModel;
	
	private static final ProvidesKey<Item> KEY_PROVIDER = new ProvidesKey<Item>() {
		@Override
		public Object getKey(Item item) {
			return item.getId();
		}
	};
	
	public LazyItemList() {
		this(false, false);
	}
	
	public LazyItemList(boolean newWindow, boolean showRating) {
		this.newWindow = newWindow;
		
		initWidget(uiBinder.createAndBindUi(this));

		// Handle scroll events.
		scrollable.addScrollHandler(new ScrollHandler() {
			public void onScroll(ScrollEvent event) {
				// If scrolling up, ignore the event.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = scrollable.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}

				HasRows display = getDisplay();
				if (display == null) {
					return;
				}
				int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					// We are near the end, so increase the page size.
					int newPageSize = Math.min(display.getVisibleRange().getLength() + incrementSize,
							display.getRowCount());
					display.setVisibleRange(0, newPageSize);
				}
			}
		});

		cell = new ItemCell(showRating) {
			protected void onItemClicked(Item entity) {
				onSelected(entity);
			};

			protected void onDeleteClicked(Item entity) {
				LazyItemList.this.remove(entity);
			};
		};

		cellList = new CellList<Item>(cell, DEFAULT_RESOURCES);
		cellList.setPageSize(incrementSize);

		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
	
		selectionModel = new MultiSelectionModel<Item>(KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);

		cellList.setLoadingIndicator(new Image(StandardImageBundle.INSTANCE.spin16White()));
		
		setDisplay(cellList);
	}

	@Override
	protected void onAttach() {
		DragAndDrop.addTarget(this);
		super.onAttach();
	}
	
	@Override
	protected void onDetach() {
		DragAndDrop.removeTarget(this);
		super.onDetach();
	}
	
	public void setNewWindow(boolean newWindow) {
		this.newWindow = newWindow;
	}
	
	public CellList<Item> getCellList() {
		return cellList;
	}
	
	/**
	 * Opens the specified item in the editor.
	 * 
	 * @param item
	 *            is the item to open.
	 */
	public void onSelected(Item entity) {
		ItemDesigner.get().open(entity.getId().toString(), newWindow);
	}

	/**
	 * Get the number of rows by which the range is increased when the scrollbar
	 * reaches the bottom.
	 * 
	 * @return the increment size
	 */
	public int getIncrementSize() {
		return incrementSize;
	}

	@Override
	public void setDisplay(HasRows display) {
		assert display instanceof Widget : "display must extend Widget";
		scrollable.setWidget((Widget) display);
		super.setDisplay(display);
	}

	/**
	 * Set the number of rows by which the range is increased when the scrollbar
	 * reaches the bottom.
	 * 
	 * @param incrementSize
	 *            the incremental number of rows
	 */
	public void setIncrementSize(int incrementSize) {
		this.incrementSize = incrementSize;
	}

	@Override
	protected void onRangeOrRowCountChanged() {
	}

	protected void remove(Item item) {
		final LazyList list = provider.getList();
		RemoteMethod remover = list.getRemover();
		if (remover != null) {
			CommandExecutor<JavaScriptObject> ex = new CommandExecutor<JavaScriptObject>();
			ex.setOnResult(new OnSuccessHandler<JavaScriptObject>() {
				@Override
				public void onSuccess(JavaScriptObject obj) {
					setValue(list);
				}
			});
			
			Item context = list.getArg(0).cast();
			ex.invoke(InvokeCommand.PATH, InvokeCommand.newInstance(remover, context, item));			
		}
	}

	public <T extends Item> void setList(List<T> list) {
		if (list == null) throw new NullPointerException();
		cellList.setRowData(list);
		cellList.redraw();
	}

	@Override
	public Editor<LazyList> asEditor() {
		return ItemListEditor.of(this);
	}

	@Override
	public void setValue(LazyList value) {
		if (value == null) throw new NullPointerException();
		RemoteMethod remover = value.getRemover();
		cell.showRemove(remover != null);
		provider = new LazyListDataProvider(value);
		provider.addDataDisplay(cellList);
	}

	@Override
	public LazyList getValue() {
		return provider.getList();
	}

	@Override
	public boolean canDrop(DragSource source) {
		final LazyList list = provider.getList();
		return source instanceof HasItem && list.getAppender() != null;
	}

	@Override
	public void cancelDrop() {
	}

	@Override
	public void drop(DragSource source, Widget drag) {
		Item item = ((HasItem)source).getItem();
		onDrop(item);
	}

	protected void onDrop(Item item) {		
		final LazyList list = provider.getList();
		RemoteMethod appender = list.getAppender();
		if (appender != null) {
			CommandExecutor<JavaScriptObject> ex = new CommandExecutor<JavaScriptObject>();
			ex.setOnResult(new OnSuccessHandler<JavaScriptObject>() {
				@Override
				public void onSuccess(JavaScriptObject obj) {
					setValue(list);					
				}
			});
			
			Item context = list.getArg(0).cast();
			ex.invoke(InvokeCommand.PATH, InvokeCommand.newInstance(appender, context, item));			
		}
		else {
			throw new RuntimeException("Dropped item but there is no appender");
		}
	}
	
	public void addProvider(AbstractDataProvider<Item> provider) {
		provider.addDataDisplay(cellList);
	}
}
