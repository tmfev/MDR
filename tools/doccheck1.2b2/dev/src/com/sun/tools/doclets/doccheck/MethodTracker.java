/*
 * @(#)MethodTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for tracking method errors.
 *
 * @author Eric Armstrong
 */
class MethodTracker extends ErrorTracker {

  /** Keep track of all objects of this type ever created. */
  static int typeCount = 0;

  MethodTracker(String aName, ErrorTracker theOwner) {
    super(aName, "Method");
    typeCount++;
    owner = theOwner;
    if (theOwner.name.indexOf('$') != -1) {
       // This is a method in an inner class or interface.
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
     if (error[level][OVERRIDES]) {
        h.li();
        h.println( "No method comment, but inherited from superclass.");
        error[level][OVERRIDES] = false;
        error[level][IMPLEMENTS] = false;
        error[level][NO_COMMENT] = false;
        error[level][NO_TEXT] = false;
        error[level][NO_EVIDENT] = false;
     }
     if (error[level][IMPLEMENTS]) {
        h.li();
        h.println( "No method comment, but inherited from interface.");
        error[level][IMPLEMENTS] = false;
        error[level][NO_COMMENT] = false;
        error[level][NO_EVIDENT] = false;
     }
     if (error[level][NO_COMMENT]) {
        h.li();
        String msg = "Method missing comment";
        if (error[level][NO_EVIDENT]) {
           // If we're at the level of a text error instead of
           // a method error, it's because we were able to generate
           // the text of the comment by inspection.
           msg = msg + ", but reasonably self-evident";
           error[level][NO_EVIDENT] = false;
        }
        msg = msg + ".";
        h.println( msg);
        error[level][NO_COMMENT] = false;
     }
     if (error[level][NO_TEXT]) {
        h.li();
        String msg = "No text in method comment";
        if (error[level][NO_EVIDENT]) {
           // If we're at the level of a text error instead of
           // a method error, it's because we were able to generate
           // the text of the comment by inspection.
           msg = msg + ", but reasonably self-evident";
           error[level][NO_EVIDENT] = false;
        }
        msg = msg + ".";
        h.println( msg);
        error[level][NO_TEXT] = false;
    }
    printTagErrors(level, h);
    printHtmlErrors(level, h);
    h.ulEnd();
  }

}
