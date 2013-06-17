package com.xclinical.mdr.client.dnd;

import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.res.MdrResources;

/**
 * A panel that the user shifts around while performing a drag and drop operation.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
class Dragbox extends PopupPanel {

	/**
	 * A style dependent name that will be applied to {@link DropTarget} while a
	 * {@link DragSource} hovers over them.
	 */
	private static final String DROPPING = "dropping";

	/**
	 * Applied to the drag widget (the widget retrieved from a call to {@link DragSource#createDragImage()}).
	 */
	private static final String DRAGGING = "dragging";

	/**
	 * The style name of this widget.
	 */
	public static final String DEFAULT_STYLENAME = "mdr-Dragbox";

	/**
	 * The cursor position when the drag and drop operation starts.
	 */
	private int dragStartX, dragStartY;

	/**
	 * The target currently under the {@link DragSource} during a drag and drop operation.
	 */
	private DropTarget currentTarget;
	
	/**
	 * The {@link DragSource} during a drag and drop operation.
	 */
	private DragSource source;

	private int options;

	static {
		MdrResources.INSTANCE.css().ensureInjected();
	}
	
	Dragbox(DragSource source, final Element element, int clientX, int clientY, int options) {
		setStyleName(MdrResources.INSTANCE.css().dragBox());

		Widget dragImage = source.createDragImage();
		if (dragImage != null) {
			add(dragImage);
			dragImage.addStyleDependentName(DRAGGING);
		}
		else {
			dragImage = new Widget() {
				{
					Element cloned = Element.as(element.cloneNode(true));
					setElement(cloned);
					addStyleName(MdrResources.INSTANCE.css().nosel());
				}
			};
			add(dragImage);
			dragImage.addStyleDependentName(DRAGGING);
		}
		
		this.source = source;
		this.options = options;
				
		dragStartX = clientX - element.getAbsoluteLeft();
		dragStartY = clientY - element.getAbsoluteTop();

		DOM.setStyleAttribute(getElement(), "width", (element.getOffsetWidth() - 24) + "px");
		DOM.setStyleAttribute(getElement(), "height", (element.getOffsetHeight() - 24) + "px");

		sinkEvents(Event.MOUSEEVENTS);
		
		setPopupPositionAndShow(new PositionCallback(){
			public void setPosition(int offsetWidth, int offsetHeight) {
				setPopupPosition(element.getAbsoluteLeft(), 
						element.getAbsoluteTop());
			}
		});		

		DOM.setCapture(getElement());
		
		source.dragStart();
	}

	private void setTarget(DropTarget target) {
		if (currentTarget != target) {
			if (currentTarget != null) {
				((UIObject)currentTarget).removeStyleDependentName(DROPPING);
				currentTarget.cancelDrop();
			}
			
			if (target != source) { // Don't drop on ourself.
				if (target != null) {
					((UIObject)target).addStyleDependentName(DROPPING);
				}		
				currentTarget = target;
			}
		}
	}
	
	@Override
	public void onBrowserEvent(Event nativeEvent) {
		switch (nativeEvent.getTypeInt()) {
		case Event.ONMOUSEOUT:
			if (currentTarget != null) {
				currentTarget.cancelDrop();
			}
			break;
			
		case Event.ONMOUSEUP: {
			DOM.releaseCapture(getElement());
			
			DropTarget target = currentTarget;
			
			setTarget(null); // UI Effect first.
			source.dragEnd(target);
			
			if (target != null) {
				target.drop(source, this);
			}
			
			hide();
			break;
		}
		case Event.ONMOUSEMOVE:
			int x = nativeEvent.getClientX() - dragStartX;
			int y = (options & DragOptions.CONSTRAIN_HORIZONTAL) != 0 ? getAbsoluteTop() : nativeEvent.getClientY() - dragStartY;

			if ((options & DragOptions.CONSTRAIN_PARENT) != 0) {
				for (Widget w = (Widget)source; w != null; w = w.getParent()) {
					if (w instanceof DropTarget) {
						setTarget((DropTarget)w);
						x = Math.max(w.getAbsoluteLeft(), x);
						y = Math.max(w.getAbsoluteTop(), y);
						x = Math.min(w.getAbsoluteLeft() + w.getOffsetWidth() - getOffsetWidth(), x);
						y = Math.min(w.getAbsoluteTop() + w.getOffsetHeight() - getOffsetHeight(), y);
						break;
					}
				}
			}
			else {
				DropTarget target = DragAndDrop.findTarget(nativeEvent.getClientX(), nativeEvent.getClientY());
				if (target != null && target.canDrop(source)) {
					setStyleName(MdrResources.INSTANCE.css().dragBoxDroppable());					
					setTarget(target);
				}
				else {
					setStyleName(MdrResources.INSTANCE.css().dragBox());										
					setTarget(null);
				}
			}
			
			setPopupPosition(x, y);
			break;
		}

		super.onBrowserEvent(nativeEvent);
	}
}
