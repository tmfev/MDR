/*
 * @(#)ConstrTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for tracking constructor errors.
 *
 * @author Eric Armstrong
 */
class ConstrTracker extends ErrorTracker {

  /** Keep track of all objects of this type ever created. */
  static int typeCount = 0;

  ConstrTracker(String aName, ErrorTracker theOwner) {
    super(aName, "Constructor");
    typeCount++;
    owner = theOwner;
    if (theOwner.name.indexOf('$') != -1) {
       // This is a constructor in an inner class.
       // Treat the original class or interface as the owner.
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
       if (error[level][DEFAULT_CONSTR]) {
          h.li();
          h.println("Default constructor not specified or missing comment.");
          error[level][DEFAULT_CONSTR] = false;
       } else {
          h.li();
          h.println( "Constructor missing comment.");
       }
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

}//ConstrTracker
