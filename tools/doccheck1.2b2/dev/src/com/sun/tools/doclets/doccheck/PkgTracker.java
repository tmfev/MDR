/*
 * @(#)PkgTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;

import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for tracking package errors.
 *
 * @author Eric Armstrong
 */
class PkgTracker extends ErrorTracker {

  /** Keep track of all objects of this type ever created. */
  static int typeCount = 0;

  /**
   * Stores the path to the package summary file.
   * The pathname is generated when the package-summary
   * page is generated, but shows up in two separate files
   * afterwards.
   */
  String filepath = "";

  protected String filepath() {
    return filepath;
  }

// Now defined this way in ErrorTracker
//  /** Return number of comments with minor errors */
//  protected int minorErrors() {
//     int minor = errorCount[TAG_ERR]
//               + errorCount[TEXT_ERR]
//               + errorCount[WARNING];
//     return minor;
//  }

  /**
   * Return the number of items that have a minor error.
   */
  protected int itemsWithMinorErrors() {
     int itemCount = 0;
     if (minorItemErrors() > 0) itemCount += 1; // this package
     ListIterator types = subIterator();
     while (types.hasNext()) {
        ErrorTracker type = (ErrorTracker) types.next();
        if (type.minorItemErrors() > 0) itemCount += 1; // class
        ListIterator mbrs = type.subIterator();
        while (mbrs.hasNext()) {
           ErrorTracker mbr = (ErrorTracker) mbrs.next();
           if (mbr.minorItemErrors() > 0) itemCount += 1; // member
        }
     }
     return itemCount;
  }

  // Save the package doc as well as name of the
  // package. The static methods in the DirectoryManager
  // use the packageDoc object to make pathnames.
  PkgTracker(String aName) {
    super(aName, "Package");
    typeCount++;
  }

  /**
   * Overrides the superclass addErr method to keep track of
   * no-comment and no-text errors on packages. Continuation
   * of the egregious hack.
   *
   * @see ErrorTracker#noCmntErr
   */
  void addErr(int level, int errID) {
     if (errID == NO_COMMENT || errID == NO_TEXT) {
       super.incrNoCmtErr();
       this.noComment = true;
     }
     super.addErr(level, errID);
  }
  boolean noComment = false;

  /**
   * Return the total count of items (types, members, etc.)
   * subsumed under this object. Overrides the ErrorTracker
   * method to go only one level deep.
   */
  protected int itemCount() {
      int typCount = subCount;
      int mbrCount = 0;
      ListIterator lit = subIterator();
      while (lit.hasNext()) {
         ErrorTracker et = (ErrorTracker) lit.next();
         mbrCount += et.subCount;
      }
      return typCount + mbrCount + 1; // add 1 for this pkg
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
        h.print( "No documentation for package. Need a <b>package.html</b> file.");
//        h.br();
//        h.print("(Copy <a href=packlage-template.html>package-template.html</a>.)");
        error[level][NO_COMMENT] = false;
     }
     if (error[level][NO_SINCE]) {
        h.li();
        h.print( "No @since tag at end of package.html.");
        error[level][NO_SINCE] = false;
     }
     printHtmlErrors(level, h);
     h.ulEnd();
  }

//   /**
//    * Overrides the version in the ErrorTracker class to
//    * display the errors returned by minorErrors() instead
//    * of minorErrorsOnly().
//    */
//   void printMinorErrsCols(HtmlTagWriter h) {
//      printCol2(minorErrors(), itemsWithMinorErrors(), h);
//   }

  void printStatsSummaryRow(HtmlTagWriter h) {
     int pkgErrs = 0;
     int typErrs = errorCount[MAJOR_ERR];
     if (noComment) {
        pkgErrs = 1;
        typErrs = typErrs - 1;
     }

     h.tr();
     h.td();  h.link(filepath, name);  h.tdEnd();
     h.dat(pkgErrs);
     h.dat(typErrs);
     h.dat(errorCount[MEMBER_ERR]);
     h.dat(errorCount[TAG_ERR]);
     h.dat(errorCount[TEXT_ERR]);
     h.dat(errorCount[WARNING]);
     h.trEnd();
  }
 
}
