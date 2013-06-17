/*
 * @(#)PkgGroupTracker.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.util.*;
import com.sun.tools.doclets.util.PathSpecifier;

/**
 * An extension of the ErrorTracker object for package
 * groups. This object performs all the same functions
 * as an ErrorTracker, but it converts the object name
 * into a pattern for a group of packages. (For example:
 * java.awt => java.awt.*). It provides a match() operation
 * (delegated to a PathSpecifier object) which tells when
 * a given package name belongs in the group.
 *
 * @author Eric Armstrong
 */
class PkgGroupTracker extends ErrorTracker {
  protected PathSpecifier packagePattern;
  protected String linkName = "";

  /**
   * Stores the path to the package summary file.
   * Used to turn the package group into a link when
   * the summary-rows are printed.
   */
  String filepath = "";

  protected String filepath() {
    return filepath;
  }

  /**
   * Construct a package-group tracking object from a package
   * name. The group-pattern is formed by appending ".*" to
   * the package name. The name() will return the pattern-form
   * (used in reports). The link() function will return the
   * original name (for use when creating links and anchors).
   *
   *
   * @param  aName  a package name, like java.awt, which becomes
   *                the pattern java.awt.*
   */
  PkgGroupTracker(String aName, String summaryFile) {
    super(aName+".*", "Package Group");
    linkName = aName;
    filepath = summaryFile+"#"+linkName;
    packagePattern = new PathSpecifier(aName+".*", ".");
  }

  /**
   * Return true if the specified package is a match for
   * this group.
   *
   * @param  pkgName  the name of the package to match
   */
  boolean match(String pkgName) {
     return packagePattern.match(pkgName, ".");
  }

}
