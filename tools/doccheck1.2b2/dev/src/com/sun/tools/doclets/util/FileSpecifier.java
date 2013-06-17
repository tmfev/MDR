/*
 * @(#)FileSpecifier.java	1.4  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.util;

import java.util.*;

/**
 * Filename specifier. Allows patterns at each level
 * of the path. For example: d*.C*Tracker matches
 * docutil.ClassTracker and docutil.ConstrTracker.
 * <blockquote>
 *   <b>Note:</b><br>
 *   A final * character in the directory-path part
 *   of the pathname matches all sub-packages.
 *   So java.awt.*.Foo* matches all files that start
 *   with "Foo" in all subpackages under java.awt.
 *   To keep from recognizing subpackages, specify
 *   "?*" as the last directory entry. The specification
 *   java.awt.?*.Foo*, therefore, matches only those
 *   classes that start with Foo that are in subpackages
 *   immediately under java.awt.
 * </blockquote>
 * By default, paths are assumed to be separated by the
 * default seperator character for the current system
 * ("/" for Unix, "\" for DOS). The path separator can
 * also be specified, both when creating the specifier
 * and when invoking the {@link #match(String, String)}
 * function. That allows a filename specifier to be created
 * with a dot-separator, for example, and yet be
 * successfully compared to a file system pathname.
 *
 * <blockquote>
 *   <b>Note:</b><br>
 *   A null pattern matches everything. The {@link #match}
 *   function always returns true. However, when a null
 *   string, is compared to any pattern, the null string
 *   always returns false. (A null string matches nothing.)
 *   When a null string is compared to a null pattern, the
 *   result is false. (The null string takes precedence.)
 * </blockquote>
 *
 * @see Specifier
 * @see PathSpecifier
 *
 * @author Eric Armstrong
 */
public class FileSpecifier {

  protected PathSpecifier pathSpec;
  protected Specifier     nameSpec;

  String separator = "";

  /**
   * Create a specifier using the default directory-seperator
   * for the current system, where the separator is used to
   * divide the input string.
   *
   * @param pattern  the pattern to match (ex: j*\Foo*Bar)
   */
  public FileSpecifier(String pattern) {
     separator = System.getProperty("file.separator");
     List pList = PathSpecifier.parsePath(pattern);
     initSpec(pList);
  }

  /**
   * Create a specifier using the specified directory-seperator,
   * where the separator is used to divide the input string.
   *
   * Note:<br>
   * The separator is assumed to contain a <i>single</i> character,
   * although it could also contain multiple characters, each of
   * which acts as a separator. On no account can it contain multiple
   * characters which act as a single separator.
   *
   *
   * @param s           the pattern to match (ex: j*\Foo*Bar)
   * @param aSeparator  the directory-name separator --
   *                    "/" for Unix, "\" for DOS, "." for packages.
   */
  public FileSpecifier(String s, String aSeparator) {
     separator = aSeparator;
     List pList = PathSpecifier.parsePath(s, separator);
     initSpec(pList);
  }

  /**
   * Create a specifier using a pre-separated list of
   * directory-name patterns.
   *
   * @param pList    a list of Strings
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  public FileSpecifier(List pList) {
     separator = System.getProperty("file.separator");
     initSpec(pList);
  }

  /**
   * Create a specifier using a pre-separated list of
   * directory-name patterns, specifying the separator that
   * was used to break them apart. (Used in toString().)
   *
   * @param pList    a list of Strings
   * @param aSeparator   the directory-name separator
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  public FileSpecifier(List pList, String aSeparator) {
     separator = aSeparator;
     initSpec(pList);
  }

  /**
   * Initialize this specifier
   *
   * @param pList    a list of Strings
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  protected void initSpec(List pList) {
     if (pList == null || pList.size() == 0) {
        nameSpec = new Specifier("*");
        pathSpec = new PathSpecifier("*");
        return;
     }
     // Unfortunately, a List doesn't have getLast()
     // Construct a LinkedList, so we can use that method.
     LinkedList pLL = new LinkedList(pList);

     String s = (String) pLL.getLast();
     nameSpec = new Specifier(s);
     pLL.removeLast();
     pathSpec = new PathSpecifier(pLL);
  }

  /**
   * Check the string argument to see if it matches this specifier,
   * where the default directory-seperator for the current system
   * is used to divide the input string into directory-name segments.
   *
   * @param s the string to match
   * @return true if the string matche this specifier
   */
  public boolean match(String s) {
     String separator = System.getProperty("file.separator");
     return match (s, separator);
  }

  /**
   * Check the string argument to see if it matches this specifier,
   * where the specified seperator is used to divide the input
   * string into directory-name segments.
   *
   * @param path   the directory-path string to match
   * @param separator  the directory-name separator --
   *                    "/" for Unix, "\" for DOS, "." for packages.
   * @return true if the string matche this specifier
   */
  public boolean match(String path, String separator) {
     if (path == null || path.equals("")) return false;

     // Check the filename
     LinkedList testList = PathSpecifier.parsePath(path, separator);
     String filename = (String) testList.getLast();
     if (!nameSpec.match(filename)) return false;

     // Check the directory path
     testList.removeLast();
     if (!pathSpec.match(testList)) return false;
     return true;
  }

  public String toString() {
     String s = pathSpec.toString();
     if (s != "") s += separator;
     s += nameSpec.toString();
     return s;
  }

}
