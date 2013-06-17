package com.xclinical.mdr.client.dnd;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controls drag and drop operations.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
public class DragAndDrop {

	private static List<UIObject> targets = new ArrayList<UIObject>();

	/**
	 * The minimum move vector that will cause dragging to start.
	 */
	private static int MOVE_THRESHOLD = 10;
	
	private static int dragStartX, dragStartY;
	
	private static Element dragging;
	
	/**
	 * Adds the specified {@link UIObject} as a target of a drag and drop operation.
	 * @param w is the element to add.
	 */
	public static void addTarget(UIObject w) {
		targets.add(w);
	}

	/**
	 * Removes the specified target.
	 * @param w is the element to remove.
	 */
	public static void removeTarget(UIObject w) {
		targets.remove(w);
	}

	private static DropTarget hitTest(UIObject w, int x, int y) {
		if (w instanceof HasWidgets) {
			DropTarget target = findTarget((HasWidgets)w, x, y);
			if (target != null) {
				return target;
			}
		}	
		
		if (w instanceof DropTarget) {
			int left = w.getAbsoluteLeft();
			int top = w.getAbsoluteTop();
			int width = w.getOffsetWidth();
			int height = w.getOffsetHeight();
			
			if (left < x && left + width > x && top < y && top + height > y) {
				return (DropTarget)w;
			}
		}
		
		return null;
	}
	
	private static DropTarget findTarget(HasWidgets hasWidgets, int x, int y) {
		for (Widget w : hasWidgets) {
			DropTarget target = hitTest(w, x, y);
			if (target != null) return target;
		}
		
		return null;
	}
	
	/**
	 * Finds a {@link DropTarget} from the specified browser coordinates.
	 * @param x is the horizontal coordinate.
	 * @param y is the vertical coordinate.
	 * @return the target at the specified location or <code>null</code>.
	 */
	public static DropTarget findTarget(int x, int y) {
		for (UIObject w : targets) {
			DropTarget target = hitTest(w, x, y);
			if (target != null) {
				return target;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds a source element.
	 * 
	 * @param source is the element to add.
	 * @param options a combination of {@link DragOptions}.
	 */
	public static void addSource(DragSource source, Widget w, int options) {
		WidgetDragController h = new WidgetDragController(source, w.getElement(), options);
		h.registerEvents(w);
	}	

	public static ElementDragController addSource(DragSource source, Element element, int options) {
		ElementDragController h = new ElementDragController(source, element, options);
		return h;
	}	
	
	private static class WidgetDragController extends DragController implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {
		public WidgetDragController(DragSource source, Element element, int options) {
			super(source, element, options);
		}
		
		@Override
		public void onMouseMove(MouseMoveEvent event) {
			if (sourceElement.isOrHasChild(event.getRelativeElement())) {
				event.stopPropagation();		
				event.preventDefault();
				handleMouseMove(event.getClientX(), event.getClientY());
			}
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			handleMouseUp();
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			if (sourceElement.isOrHasChild(event.getRelativeElement())) {
				event.stopPropagation();
				handleMouseDown(event.getClientX(), event.getClientY());
			}
		}
		
		public void registerEvents(Widget w) {
			w.addDomHandler(this, MouseDownEvent.getType());
			w.addDomHandler(this, MouseUpEvent.getType());
			w.addDomHandler(this, MouseMoveEvent.getType());
		}
	}

	public static class ElementDragController extends DragController {
		public ElementDragController(DragSource source, Element element, int options) {
			super(source, element, options);
		}
		
		public void onMouseMove(NativeEvent event) {
				event.stopPropagation();		
				event.preventDefault();
				handleMouseMove(event.getClientX(), event.getClientY());
		}

		public void onMouseUp(NativeEvent event) {
			handleMouseUp();
		}

		public void onMouseDown(NativeEvent event) {
				event.stopPropagation();
				handleMouseDown(event.getClientX(), event.getClientY());
		}		
	}
	
	private static class DragController {
		private final int options;
		
		private final DragSource source;
		
		protected final Element sourceElement;
		
		public DragController(DragSource source, Element element, int options) {
			this.source = source;
			this.options = options;
			this.sourceElement = element;
		}
		
		public void handleMouseMove(int clientX, int clientY) {
			if (sourceElement == dragging) {
				int cx = dragStartX - clientX;
				int cy = dragStartY - clientY;
				int move = cx * cx + cy * cy;
				if (move > MOVE_THRESHOLD) {
					dragging = null;
					new Dragbox(source, sourceElement, dragStartX, dragStartY, options);
				}
			}
		}

		public void handleMouseUp() {
			dragging = null;
		}

		public void handleMouseDown(int clientX, int clientY) {
			if (sourceElement != dragging) {
				dragStartX = clientX;
				dragStartY = clientY;
				dragging = sourceElement;
			}
		}		
	}	
	
}
