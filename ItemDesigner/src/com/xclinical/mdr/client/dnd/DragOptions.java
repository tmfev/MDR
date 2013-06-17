package com.xclinical.mdr.client.dnd;

/**
 * Defines flags to be used in drag and drop operations.
 * 
 * @author Michael Schollmeyer (michael@mictale.com)
 * @version $Revision: 186 $
 */
public interface DragOptions {

	/**
	 * Constraints dragging to horizontal moves.
	 */
	public static final int CONSTRAIN_HORIZONTAL = 1 << 0;
	
	/**
	 * Constraints dragging to the parent widget.
	 */
	public static final int CONSTRAIN_PARENT = 1 << 1;
}
