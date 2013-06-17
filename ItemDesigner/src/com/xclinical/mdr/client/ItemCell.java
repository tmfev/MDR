package com.xclinical.mdr.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DragAndDrop.ElementDragController;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.res.ItemResources;

public class ItemCell extends AbstractCell<Item> {

	private final ItemRenderer renderer;

	private ElementDragController dragController;
	
	public ItemCell(boolean showRating) {
		this(new ItemRenderer(false, showRating));
	}

	public ItemCell(ItemRenderer renderer) {
		super("click", "blur", "mousemove", "mouseup", "mousedown");
		this.renderer = renderer;
	}

	public void showRemove(boolean show) {
		renderer.showRemove(show);
	}
	
	@Override
	public boolean handlesSelection() {
		return true;
	}

	@Override
	public void render(Cell.Context context, Item value, SafeHtmlBuilder sb) {
		renderer.render(value, sb);
	}

	@Override
	public void onBrowserEvent(Cell.Context context, Element parent, Item value,
			NativeEvent event, ValueUpdater<Item> valueUpdater) {
		
		String eventType = event.getType();
		if ("click".equals(eventType)) {
			EventTarget eventTarget = event.getEventTarget();
			if (Element.is(eventTarget)) {
				Element target = Element.as(eventTarget);
				final String targetClass = target.getClassName();
				if (ItemResources.INSTANCE.style().removeButton().equals(targetClass)) {
					onDeleteClicked(value);
				}
				else {
					onItemClicked(value);
				}
			}
		}
		else if ("mousedown".equals(eventType)) {
			dragController = DragAndDrop.addSource(new ItemDragSource(value), parent, 0);
			dragController.onMouseDown(event);
		}
		else if ("mouseup".equals(eventType)) {
			if (dragController != null) {
				dragController.onMouseUp(event);
				dragController = null;
			}
		}
		else if ("mousemove".equals(eventType)) {
			if (dragController != null) {
				dragController.onMouseMove(event);
			}
		}
		else {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	protected void onItemClicked(Item entity) {		
	}
	
	protected void onDeleteClicked(Item entity) {
		throw new UnsupportedOperationException();
	}
}
