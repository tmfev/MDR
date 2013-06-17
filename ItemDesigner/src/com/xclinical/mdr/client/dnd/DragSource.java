package com.xclinical.mdr.client.dnd;

import com.google.gwt.user.client.ui.Widget;

/**
 * Implemented by {@link Widget}s that can act as a source of a drag and drop operation.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
public interface DragSource {

	/**
	 * Retrieves the drag image.
	 * 
	 * If the implementation returns <code>null</code>, the framework will create
	 * a generic image.
	 * 
	 * @return the drag image.
	 */
	Widget createDragImage();
	
	/**
	 * Called when a drag operation starts.
	 */
	void dragStart();
	
	/**
	 * Called when a drag operation terminates.
	 * 
	 * This method is called is a sequence of a drag and drop operation
	 * just before {@link DropTarget#drop(DragSource, Widget)} is called.
	 * 
	 * @param target is the target of the drop operation, if any.
	 */
	void dragEnd(DropTarget target);
}
