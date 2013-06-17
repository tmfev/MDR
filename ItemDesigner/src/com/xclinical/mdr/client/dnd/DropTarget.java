package com.xclinical.mdr.client.dnd;

import com.google.gwt.user.client.ui.Widget;

/**
 * Implemented by {@link Widget}s that act as targets of a drag and drop operation.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 *
 */
public interface DropTarget {

	/**
	 * Tests if the current target is capable of receiving the specified
	 * source.
	 * 
	 * @param source is the source of a drag and drop operation.
	 * 
	 * @return <code>true</code> iff the curent target is capable of receiving
	 * 		the specified source.
	 */
	boolean canDrop(DragSource source);
	
	void cancelDrop();
	
	/**
	 * Drops the specified source onto the current target.
	 * 
	 * @param source is the source of the drag and drop operation.
	 * @param drag is the widget that is currently dragging.
	 */
	void drop(DragSource source, Widget drag);
}
