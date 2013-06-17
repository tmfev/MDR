package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.dnd.DragSource;
import com.xclinical.mdr.client.dnd.DragAndDrop;
import com.xclinical.mdr.client.dnd.DropTarget;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.res.StandardImageBundle;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

public final class ItemLink<T extends Item> extends Composite implements IsEditor<Editor<T>>, HasValue<T>, DragSource,
		DropTarget, HasItem, HasKey {

	interface Binder extends UiBinder<Widget, ItemLink<?>> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Style extends CssResource {
		String dropOver();
		
		String disabled();
	}

	@UiField
	Anchor anchor;

	@UiField
	Image dropDown;
	
	@UiField
	Style style;

	private T entity;

	private boolean showId;

	private boolean readOnly;
	
	private Key type;

	private boolean enabled = true;

	public ItemLink() {
		initWidget(uiBinder.createAndBindUi(this));

		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (entity != null) {
					ItemDesigner.get().open(entity.getId().toString(), false);
				}
			}
		});
		anchor.setText("(null)");

		Handlers h = new Handlers();
		dropDown.addMouseDownHandler(h);
		dropDown.addMouseUpHandler(h);
		dropDown.addMouseOverHandler(h);
		dropDown.addMouseOutHandler(h);
		
		DragAndDrop.addSource(this, this, 0);
	}

	@UiHandler("dropDown")
	protected void onDropDownClicked(ClickEvent event) {
		if (enabled) {
			SearchPopupPanel panel = new SearchPopupPanel(this, type);
			panel.showRelativeTo(this);
		}
	}
	
	public void setRestrictType(String type) {
		this.type = Key.parse(type);
	}
	
	public void setShowId(boolean show) {
		showId = show;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		dropDown.setVisible(!readOnly);
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

	@Override
	public Editor<T> asEditor() {
		return ItemLinkEditor.of(this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	@Override
	public void setValue(T value) {
		entity = value;
		ItemRenderer r = new ItemRenderer(showId, false);
		anchor.setHTML(r.render(value));
	}

	@Override
	public void setValue(T value, boolean fireEvents) {				
		setValue(value);
		
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}		
	}
	
	@Override
	public T getValue() {
		return entity;
	}

	@Override
	public Widget createDragImage() {
		ItemRenderer r = new ItemRenderer(showId, false);
		HTML html = new HTML(r.render(entity));
		html.addStyleName(MdrResources.INSTANCE.css().nosel());
		return html;
	}

	@Override
	public void dragStart() {
	}

	@Override
	public void dragEnd(DropTarget target) {
	}

	@Override
	public boolean canDrop(DragSource source) {
		if (enabled && !readOnly && source != this && source instanceof HasItem) {
			if (type != null) {
				Item toDrop = ((HasItem)source).getItem();
				if (!type.getName().equals(toDrop.getKey().getName())) {
					return false;
				}
			}
			
			addStyleName(style.dropOver());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void cancelDrop() {
		removeStyleName(style.dropOver());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DragSource source, Widget drag) {
		HasItem hasItem = (HasItem) source;
		setValue((T)hasItem.getItem(), true);
	}

	@Override
	public Item getItem() {
		return entity;
	}

	@Override
	public Key getKey() {
		return entity.getKey();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if (enabled) {
			anchor.removeStyleName(style.disabled());
		}
		else {
			anchor.addStyleName(style.disabled());			
		}
		
		anchor.setEnabled(enabled);
	}
	
	private final class Handlers implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler {

		@Override
		public void onMouseUp(MouseUpEvent event) {
			if (enabled) {
				dropDown.setResource(StandardImageBundle.INSTANCE.dropDown());
			}
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			if (enabled) {
				dropDown.setResource(StandardImageBundle.INSTANCE.dropDownHot());
			}
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			if (enabled) {
				dropDown.setResource(StandardImageBundle.INSTANCE.dropDownLight());
			}
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (enabled) {
				dropDown.setResource(StandardImageBundle.INSTANCE.dropDown());
			}
		}
	}
}
