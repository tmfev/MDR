package com.xclinical.mdr.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.util.JsList;

/**
 * Shows items in a list.
 * 
 * @author ms@xclinical.com
 */
public class ItemList extends AbstractPager implements IsEditor<Editor<JsArray<? extends Item>>>,
		TakesValue<JsArray<? extends Item>> {

	interface MyUiBinder extends UiBinder<Widget, ItemList> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private JsList<? extends Item> list;

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
	private static final int DEFAULT_INCREMENT = 40;

	/**
	 * The increment size.
	 */
	private int incrementSize = DEFAULT_INCREMENT;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	protected final CellList<Item> cellList;

	private boolean newWindow;
	
	public ItemList() {
		this(false, new ItemRenderer());
	}
	
	public ItemList(boolean newWindow, ItemRenderer renderer) {
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

		final ItemCell cell = new ItemCell(renderer) {
			@Override
			protected void onItemClicked(Item entity) {
				onSelected(entity);
			};

			protected void onDeleteClicked(Item entity) {
				ItemList.this.remove(entity);
			};
		};

		cellList = new CellList<Item>(cell, DEFAULT_RESOURCES);
		cellList.setPageSize(incrementSize);

		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		setDisplay(cellList);
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

	protected void remove(Item entity) {
		throw new UnsupportedOperationException();
	}

	public <T extends Item> void setList(List<T> list) {
		if (list == null) throw new NullPointerException();
		cellList.setRowData(list);
		cellList.redraw();
	}

	@Override
	public Editor<JsArray<? extends Item>> asEditor() {
		return ItemListEditor.of(this);
	}

	@Override
	public void setValue(JsArray<? extends Item> value) {
		if (value == null) throw new NullPointerException();
		list = new JsList<Item>((JsArray<Item>) value);
		setList(list);
	}

	@Override
	public JsArray<? extends Item> getValue() {
		return list.asArray();
	}
}
