package com.xclinical.mdr.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;

public class MinimizedWindowList extends Composite implements DropTarget {

	private HorizontalPanel panel;
	
	public MinimizedWindowList() {
		panel = new HorizontalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LOCALE_START);
		panel.setWidth("100%");
		panel.setHeight("3em");
		initWidget(panel);
		
		DOM.setStyleAttribute(panel.getElement(), "padding", "4pt");
		DOM.setStyleAttribute(panel.getElement(), "backgroundColor", "rgb(208,228,246)");
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
	
	private void addLink(Item item) {
		final ItemLink<Item> link = new ItemLink<Item>();
		link.setShowId(true);
		link.setValue(item);
		panel.add(link);
	}
	
	@Override
	public boolean canDrop(DragSource source) {
		if (source != this && source instanceof HasItem) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void cancelDrop() {
	}

	@Override
	public void drop(DragSource source, Widget drag) {
		HasItem hasItem = (HasItem) source;
		addLink(hasItem.getItem());
	}
}
