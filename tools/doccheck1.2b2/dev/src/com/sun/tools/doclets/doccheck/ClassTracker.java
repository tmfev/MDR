/*
 * @(#)ClassTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for tracking class errors.
 *
 * @author Eric Armstrong
 */
class ClassTracker extends ErrorTracker {

  /** Keep track of all objects of this type ever created. */
  static int typeCount = 0;
  static int innerCount = 0;

  ClassTracker(String aName, ErrorTracker theOwner) {
     super(aName, "Class");
     typeCount++;
     owner = theOwner;
     if (aName.indexOf('$') != -1) {
        // An inner class
        innerCount++;
        if (theOwner.name.indexOf('$') != -1) {
           // This is an inner-inner class
           // Treat the original class as the owner
           owner = theOwner.owner;
        }
     }
     owner.subCount++;
  }

  /**
   * Print error messages for a level.
   * @param level an int specifying the error message level (1..4)
   * @param h an HtmlTagWriter object to write to
   */
  void printErrorMessages(int level, HtmlTagWriter h) {
    h.ul();
    if (error[level][NO_COMMENT]) {
        h.li();
        h.println( "Class missing comment.");
        error[level][NO_COMMENT] = false;
    }
    if (error[level][NO_TEXT]) {
        h.li();
        h.println( "No text in class comment.");
        error[level][NO_TEXT] = false;
    }
    printTagErrors(level, h);
    printHtmlErrors(level, h);
    h.ulEnd();
  }

}