/*
 * @(#)ErrorTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * Node for use in an error-tracking hierarchy.
 * <p>
 * Implementation and usage notes:<ul>
 * <li>An error-tracking object is created for each package,
 *     class, interface, field, constructor, and method.
 * <li>Each object contains a list of subobjects. A subobject
 *     for a package could be an interface object, a class
 *     object, or an error object.
 * <li>When an error object is added to an error-tracker using
 *     the addErr() method, the error-count is updated immediately
 * <li>Other kinds of objects are added after their final error
 *     counts are determined, using addSub(). Their error counts
 *     are then accumulated in the current object's counts.<br>
 *     Note: 22 Sep 99<br>
 *     addSub() used to only add subelements that had an error.
 *     But that messes up the totals calculation. The number of
 *     total number of packages is fine, but the total number of
 *     classes was kept only for those packages that had errors.
 *     (Similary, total number of members was kept only for classes
 *     that had errors.) To remedy that error, addSub now always
 *     adds the subitem. The error-reporting procedures must
 *     therefore be alert to the possiblity of entries that have
 *     no errors in the lists it processes.
 * </ul>
 *
 * @author Eric Armstrong
 */
class ErrorTracker implements DocCheckConstants {

  String name;
  String typeName;

  // Template for new and corrected comments
  Form[] form = new Form[ERR_CATEGORY_COUNT];

  /**  Error counts for this item only. (Shallow) */
  int[] itemErrs = new int[ERR_CATEGORY_COUNT];  // This item only
  /**  Non-cumulative item count for subs. (Shallow) */
  int[] subErrs = new int[ERR_CATEGORY_COUNT];   // Sub items only

  /**  Cumulative error count. (Deep) */
  int[] errorCount = new int[ERR_CATEGORY_COUNT];   // this + subs
  boolean[][] error = new boolean[ERR_CATEGORY_COUNT][ERR_TYPE_COUNT];
  String[] htmlError = new String[ERR_CATEGORY_COUNT];

  List sub = new LinkedList();

  /**
   * Keeps track of all subitems that ever existed for this item.
   * For packages, keeps a count of classes and interfaces.
   * For interfaces, keeps a count of methods, fields, sub-interfaces,
   *     subinterface members, sub-subinterface members, etc.
   * For classes, keeps the same numbers as interfaces, along with
   *     constructors, subclasses, subclass members, sub-sub etc.
   */
  int subCount = 0;

  /** The parent package, class, or interface this one belowgs to. */
  ErrorTracker owner;

  /**
   * Keep track of no comment errors on packages, so we can
   * back them out from other No Comment errors on classes
   * and interfaces. This is an egregious hack, perpetrated
   * in the name of expediency. Ideally, DocUtil should be
   * modified to create a separate index for package errors,
   * and all the reporting code should be modified to reflect
   * that.
   */
  static int noCmtErr = 0;
  void incrNoCmtErr() {
     noCmtErr++;
  }

  ErrorTracker(String aName, String aTypeName) {
    name = aName;
    typeName = aTypeName;
  }

  /**
   * Add an item to the sub list if it has errors.
   */
  void addSub(ErrorTracker et) {
    sub.add(et);
    for (int i=0; i<ERR_CATEGORY_COUNT; i++) {
       errorCount[i] += et.errorCount[i];
       subErrs[i] += et.itemErrs[i];
    }
  }

  /**
   * Return true if there are any errors.
   */
  boolean hasError() {
    for (int i=0; i<ERR_CATEGORY_COUNT; i++) {
       if (errorCount[i] != 0) return true;
    }
    return false;
  }

  /**
   * Return true if there are any errors at this level.
   *
   * @param level  an int specifying the error level to check
   */
  boolean hasError(int level) {
     if (errorCount[level] != 0) return true;
     return false;
  }
  /**
   * Return true if there is a template-form for this level.
   *
   * @param level  an int specifying the error level to check
   */
  boolean hasForm(int level) {
     if (form[level] != null) return true;
     return false;
  }

  /** Return number of missing comment errors */
  int missingCommentErrors() {
     int missing = errorCount[MAJOR_ERR] + errorCount[MEMBER_ERR];
     return missing;
  }

  /**
   * Return number of minor errors in this item & all children.
   */
  protected int minorErrors() {
     int minor = errorCount[TAG_ERR]
               + errorCount[TEXT_ERR]
               + errorCount[WARNING];
     return minor;
  }

  /**
   * Return number of minor errors in this item.
   */
  protected int minorItemErrors() {
     int minor = itemErrs[TAG_ERR]
               + itemErrs[TEXT_ERR]
               + itemErrs[WARNING];
     return minor;
  }


  /**
   * Return the number of items that have a minor error.
   * Overridden in PkgTracker to process the item error counts.
   */
  protected int itemsWithMinorErrors() {
     int itemCount = 0;
     ListIterator pkgs = subIterator();
     while (pkgs.hasNext()) {
       ErrorTracker pkg = (ErrorTracker) pkgs.next();
       itemCount += pkg.itemsWithMinorErrors();
     }
     return itemCount;
  }

  /**
   * Return true if there is an error message at this level.
   *
   * @param level  an int specifying the error level to check
   */
  boolean hasMessage(int level) {
     for (int i=0; i<ERR_TYPE_COUNT; i++) {
       if (error[level][i]) return true;
     }
     return false;
  }

  /**
   * Return an iteration over the sub list.
   */
  ListIterator subIterator() {
     return sub.listIterator();
  }

  //--------------TEMPLATE HANDLING--------------------
  /** Start. Delegated to Form object. */
  void formStart(int level) {
     if (form[level] == null) form[level] = new Form();
     form[level].start();
  }
  /** End. Delegated to Form object. */
  void formEnd(int level) {
     if (form[level] == null) form[level] = new Form();
     form[level].end();
  }
  /** Add a line of text. Delegated to Form object. */
  void formLine(int level, String s) {
     if (form[level] == null) form[level] = new Form();
     form[level].addLine(s);
  }
  void formBadLine(int level, String s) {
     formLine(level, Form.badText(s) );
  }

  /** Add a tag. Delegated to Form object. */
  void formTag(int level, String tag, int tagType) {
     if (form[level] == null) form[level] = new Form();
     form[level].addTag(tag, tagType);
  }

  /** Add a bad tag. Delegated to Form object. */
  void formBadTag(int level, String tag, int tagType) {
     if (form[level] == null) form[level] = new Form();
     tag = Form.badText(tag);
     form[level].addTag(tag, tagType);
  }

  /** Print the comment template. Delegated to Form object. */
  void formPrint(int level, HtmlTagWriter h) {
     boolean trace = false;
     if (trace) System.out.println(name+": Printing form");
     if (form[level] == null) {
        if (trace) System.out.println("--form dne");
        return;
     }
     form[level].print(h);
  }

  //--------------ERROR HANDLING--------------------

  /** Add error message and keep an error count. */
  void addErr(int level, int errID) {
    //System.out.println("  addErr: "+name+", level "+level+", error "+errID);

    // Count HTML errors only once.
    // NOTE: If we do this for *all* errors, instead of just HTML
    //       errors, then the error counts may well reduce to "number
    //       of items with a particular type of error", rather than
    //       a count of errors. (A method with 5 parameter tag errors
    //       would produce a count of "1" -- one method with param
    //       tag errors -- rather than 5 param tag errors.
    // NO. Tried it. Seems not to work. java.applet with 1 tag error
    //     and one text error produced a summary of "1" instead of 2.
    //     It's not clear why that happened, but it kills this idea
    //     for now.
    if (errID == HTML_ERROR && error[level][errID]) return;

    error[level][errID] = true;

    // Ignore "errors" that give location of the error,
    // rather than specifying the nature of the error.
    // to prevent duplicate counts.)
    if (errID == HTML_FIRST) return;
    if (errID == HTML_BODY) return;
    if (errID == DEFAULT_CONSTR) return;

    // These duplicate a No Comment or Missing Text Error
    if (errID == IMPLEMENTS) return;
    if (errID == OVERRIDES)  return;
    if (errID == NO_EVIDENT) return;

    if (error[level][NO_COMMENT]) {
       // Missing tag "errors" may occur when no comment exists.
       // Ignore them.
       if (errID == TAG_MISSING) return;
    }

    errorCount[level]++;
    itemErrs[level]++;
    //System.out.println("  Level "+level+" error count="+errorCount[level]);
  }

  /** Save a string for HTML error messages. */
  void addHtmlErrLine(int level, String s) {
     if (htmlError[level]==null) {
        htmlError[level] = s;
     } else {
        htmlError[level]+="<br>"+s;
     }
  }

  void printLinkedRow(String filelink, HtmlTagWriter h) {
     h.tr();
     h.td();
     h.println("<a href=\"" + filelink +"\">"
               + name + "</a>");
     h.tdEnd();
     for (int i=TYPE_ERR; i<ERR_CATEGORY_COUNT; i++) {
        printCount(errorCount[i], filelink+"#err"+i, h);
     }
     h.trEnd();
  }

  /**
   * @return numerator divided by denominator with no decimal places.
   */
  public static long percent0(int num, int denom) {
     if (denom == 0) return -1;

     // Assign and divide in separate steps.
     // Otherwise, the intermediate result is an int, which
     // truncates the decimal places. Since this is a percentage,
     // the produces zero as a result.
     double t = num;
     t /= denom;
     return Math.round(t * 100);  // round to 2 digits -- nn%
  }

  /**
   * @return a string with a percentage (no decimal places)
   * that distinguishes between 0% and <1%
   */
  public static String percentString0(int num, int denom) {
     if (denom == 0) return "-1";
     if (num == 0) return "--";

     // Assign and divide in separate steps.
     // Otherwise, the intermediate result is an int, which
     // truncates the decimal places. Since this is a percentage,
     // the produces zero as a result.
     double t = num;
     t /= denom;
     long n = Math.round(t * 100);  // round to 2 digits -- nn%
     if (n == 0) return "<1%";
     return n+"%";
  }

  /**
   * @return numerator divided by denominator with 1 decimal place.
   */
  public static double percent1(int num, int denom) {
     if (denom == 0) return -1;

     // Otherwise, the intermediate result is an int, which
     // truncates the decimal places. Since this is a percentage,
     // the produces zero as a result.
     double t1 = num;
     t1 /= denom;
     long t2 = Math.round(t1 * 1000);   // round to 3 digits -- nn.n%
     t1 = t2;
     t1 /= 10;
     return t1;
  }

  void printCount(int n, String link, HtmlTagWriter h) {
     if (n == 0) {
        h.dat(0);
     } else {
        h.tdAlign("Right");
        h.link(link, ""+n);
        h.tdEnd();
     }
  }

  /**
   * Does nothing. Must be overridden by subclasses to print
   * error messages for a given source code entity.
   * (The exact messages to print depends on what type of code
   *  entity we are tracking.)
   * <p>
   * The only reason this method isn't abstract is that an
   * error tracker is also create for the summary page. Since
   * that page has no error messages associated with it, this
   * method is not required.)
   *
   * @param level an int specifying the error message level (1..4)
   * @param h a com.sun.javadoc.HtmlWriter object to write to
   */
  void printErrorMessages(int level, HtmlTagWriter h) {
  }

  /**
   * Print tag error messages.
   *
   * @param level an int specifying the error level to print
   * @param h an HtmlTagWriter object to write to
   */
  void printTagErrors(int level, HtmlTagWriter h) {
    if (error[level][TAG_MISSING]) {
       h.li();
       h.println( "Missing tag.");
       error[level][TAG_MISSING] = false;
    }
    if (error[level][TAG_TEXT]) {
       h.li();
       h.println( "Tag missing text.");
       error[level][TAG_TEXT] = false;
    }
    if (error[level][TAG_INVALID]) {
       h.li();
       h.println( "Invalid tag.");
       error[level][TAG_INVALID] = false;
    }
    if (error[level][TAG_EXTRA]) {
       h.li();
       h.println( "Extraneous tag.");
       error[level][TAG_EXTRA] = false;
    }
    if (error[level][TAG_MULTVER]) {
       h.li();
       h.println( "Multiple @version tags.");
       error[level][TAG_MULTVER] = false;
    }
    if (error[level][RET_MISSING]) {
       h.li();
       h.println( "Missing @return tag.");
       error[level][RET_MISSING] = false;
    }
    if (error[level][RET_INVALID]) {
       h.li();
       h.println( "Invalid @return tag.");
       error[level][RET_INVALID] = false;
    }
    if (error[level][RET_TEXT]) {
       h.li();
       h.println( "Missing text in @return tag.");
       error[level][RET_TEXT] = false;
    }
    if (error[level][RET_EXTRA]) {
       h.li();
       h.println( "Extraneous @return tag.");
       error[level][RET_EXTRA] = false;
    }
    if (error[level][EXC_MISSING]) {
       h.li();
       h.println( "Missing @throws tag.");
       error[level][EXC_MISSING] = false;
    }
    if (error[level][EXC_INVALID]) {
       h.li();
       h.println( "Invalid @throws/@exception tag.");
       error[level][EXC_INVALID] = false;
    }
    if (error[level][EXC_TEXT]) {
       h.li();
       h.println( "Missing text in @throws/@exception tag.");
       error[level][EXC_TEXT] = false;
    }
    if (error[level][EXC_EXTRA]) {
       h.li();
       h.println( "Extraneous @throws/@exception tag.");
       error[level][EXC_EXTRA] = false;
    }
    if (error[level][SEE_INVALID]) {
       h.li();
       h.println( "Invalid @see tag.");
       error[level][SEE_INVALID] = false;
    }
    if (error[level][SEE_MISSING]) {
       h.li();
       h.println( "Missing @see tag.");
       error[level][SEE_MISSING] = false;
    }
    if (error[level][SEE_TEXT]) {
       h.li();
       h.println( "Missing text in @see tag.");
       error[level][SEE_TEXT] = false;
    }
  }

  /**
   * Print HTML error messages.
   *
   * @param level an int specifying the error level to print
   * @param h an HtmlTagWriter object to write to
   */
  void printHtmlErrors(int level, HtmlTagWriter h) {
    if (error[level][HTML_FIRST]) {
       h.li();
       h.println("Html Error in First Sentence");
       error[level][HTML_FIRST] = false;
       error[level][HTML_ERROR] = false;
    }
    if (error[level][HTML_BODY]) {
       h.li();
       h.println("Html Error in Body of Comment");
       error[level][HTML_BODY] = false;
       error[level][HTML_ERROR] = false;
    }
    if (error[level][HTML_NO_PERIOD]) {
       h.br();
       h.println("--Missing a period");
       error[level][HTML_NO_PERIOD] = false;
    }
    if (error[level][HTML_LIST]) {
       h.br();
       h.println("--First sentence contains a list");
       error[level][HTML_LIST] = false;
    }
    if (error[level][HTML_ERROR]) {
       h.br();
       h.println("--HTML error");
       error[level][HTML_ERROR] = false;
    }
    if (htmlError[level] != null && htmlError[level] != "") {
       h.br();
       h.print(htmlError[level]);
    }
  }
  
   void printMajorErrsRow(HtmlTagWriter h) {
      h.tr();
      printColFilename(h);
      printCol2(missingCommentErrors(), h);
      h.trEnd();
   }
   void printMinorErrsRow(HtmlTagWriter h) {
      h.tr();
      printColFilename(h);
      printCol2(itemsWithMinorErrors(), h);
      h.trEnd();
   }
   void printMajorErrsCols(HtmlTagWriter h) {
      printCol2(missingCommentErrors(), h);
   }
   void printMinorErrsCols(HtmlTagWriter h) {
      printCol2(itemsWithMinorErrors(), h);
   }

  // Overridden in PkgTracker to return the filename
  // Here, it returns a null string so the print routine
  // knows not to make a link.
  protected String filepath() {
    return "";
  }

  /**
   * Return the total count of items (types, members, etc.)
   * subsumed under this object. This method is overridden
   * in the PkgTracker class to do the actual calculation.
   */
  protected int itemCount() {
      int itemCount = 0;
      ListIterator pkgs = subIterator();
      while (pkgs.hasNext()) {
         ErrorTracker pkg = (ErrorTracker) pkgs.next();
         itemCount += pkg.itemCount();
      }
      return itemCount;
  }
 
  /**
   * Print a two columns containing an error-count
   * and a percentage.
   *
   * @param n     the number of errors of a given type to display
   * @return true if the error count was non-zero
   *              and the row was printed
   */
  void printCol2(int n, HtmlTagWriter h) {
     //if (n == 0) return;
     // Count of items
     int itemCount = itemCount();
     h.dat(n);
     h.dat(percentString0(n, itemCount));
  }

  /**
   * Print a column containing filename, generating as a
   * link, if not null.
   *
   * @return true if the error count was non-zero
   *              and the row was printed
   */
  void printColFilename(HtmlTagWriter h) {
     String filepath = filepath();
     if (filepath.equals("")) {
        h.td(); h.print(name); h.tdEnd();
     } else {
        h.td(); h.link(filepath, name); h.tdEnd();
     }
  }

} // End class
