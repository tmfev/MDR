/*
 * @(#)FieldTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for tracking field errors.
 *
 * @author Eric Armstrong
 */
class FieldTracker extends ErrorTracker {

  /** Keep track of all objects of this type ever created. */
  static int typeCount = 0;

  FieldTracker(String aName, ErrorTracker theOwner) {
    super(aName, "Field");
    typeCount++;
    owner = theOwner;
    if (theOwner.name.indexOf('$') != -1) {
       // This is a field in an inner class.
       // Treat the original class as the owner.
       owner = theOwner.owner;
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
       h.println( "Field missing comment.");
       error[level][NO_COMMENT] = false;
    }
    if (error[level][NO_TEXT]) {
        h.li();
        h.println( "No text in field comment.");
        error[level][NO_TEXT] = false;
    }
    printTagErrors(level, h);
    printHtmlErrors(level, h);
    h.ulEnd();
  }

}
