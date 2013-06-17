/*
 * @(#)PathSpecifier.java	1.3  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.util;

import java.util.*;

/**
 * Path name specifier. Allows patterns at each level
 * of the path. For example: j*.s* matches java.security
 * and javax.swing. A final * character matches all
 * sub-packages. So java.awt.* matches all subpackages of
 * java.awt.
 * <blockquote>
 *   <b>Note:</b><br>
 *   To keep from recognizing subpackages, specify
 *   "?*" as the last entry. The "?" is intended to
 *   reflect its usual meaning as "any character".
 *   Although the "?" pattern is not recognized in
 *   general, it's use here serves to distinguish
 *   "all subdirectories" (*) from "all directories
 *   at this level (?*).
 * </blockquote>
 * Here is the table of options for processing various
 * combinations of packages from the swing hierarchy:<pre>
 *            swing
 *              |
 *          +---+---+
 *        table    text
 *                   |
 *                  html
 *</pre><dl>
 *<dt>javax.swing
 *<dd>The swing package only.
 *<dt>javax.swing.*
 *<dd>The swing package and all sub packages: table, text,
 *    and text.html.
 *<dt>javax.swing.*.*
 *<dd>All sub packages of swing, not including swing itself
 *    and all their subs, recursively: table, text, table.html.
 *<dt>javax.swing.?*
 *<dd>Direct descendants of swing, not including subpackages
      and not including swing itself: table, text.
 *<dt>javax.swing + javax.swing.?*
 *<dd>The swing package and any direct descendants.
 *</dl>
 * By default, paths are assumed to be separated by the
 * default seperator character for the current system
 * ("/" for Unix, "\" for DOS). The path separator can
 * also be specified, both when creating the specifier
 * and when invoking the {@link #match(String, String)}
 * function. That allows a package-specifier to be created
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
 * @see FileSpecifier
 *
 * @author Eric Armstrong
 */
public class PathSpecifier {

  protected LinkedList patternList = new LinkedList();

  /**
   * Set true when the pattern to match ends with "*",
   * as in "java.awt.*".
   */
  protected boolean openEnded = false;

  String separator = "";

  /**
   * Create a specifier using the default directory-seperator
   * for the current system, where the separator is used to
   * divide the input string into directory-name patterns.
   *
   * @param pattern  the pattern to match (ex: j*\s*), where
   *                 a null pattern or empty string matches everything
   */
  public PathSpecifier(String pattern) {
     separator = System.getProperty("file.separator");
     List pList = parsePath(pattern);
     initPatternList(pList);
  }

  /**
   * Create a specifier using the specified directory-seperator,
   * where the separator is used to divide the input string into
   * directory-name patterns.
   *
   * Note:<br>
   * The separator is assumed to contain a <i>single</i> character,
   * although it could also contain multiple characters, each of
   * which acts as a separator. On no account can it contain multiple
   * characters which act as a single separator.
   *
   *
   * @param s           the pattern to match (ex: j*\s*), where
   *                    a null pattern or empty string matches everything
   * @param aSeparator  the directory-name separator --
   *                    "/" for Unix, "\" for DOS, "." for packages.
   */
  public PathSpecifier(String s, String aSeparator) {
     separator = aSeparator;  // Save for use in toString()
     List pList = parsePath(s, aSeparator);
     initPatternList(pList);
  }

  /**
   * Create a specifier using a pre-separated list of
   * directory-name patterns.
   *
   * @param pList    a list of Strings
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  public PathSpecifier(List pList) {
     separator = System.getProperty("file.separator");
     initPatternList(pList);
  }

  /**
   * Create a specifier using a pre-separated list of
   * directory-name patterns, plus the separator that
   * was used to split them apart (for use when gluing
   * them back together with toString().
   *
   * @param pList     a list of Strings
   * @param aSeparator   the directory-name separator
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  public PathSpecifier(List pList, String aSeparator) {
     separator = aSeparator;
     initPatternList(pList);
  }

  /**
   * Divide the string argument into directory-name patterns
   * using the default directory-seperator for the current system.
   *
   * @param path    the pathname to parse
   */
  public static LinkedList parsePath(String path) {
     return parsePath(path, System.getProperty("file.separator"));
  }

  /**
   * Divide the string argument into directory-name patterns
   * using the specified directory-seperator.
   *
   * Note:<br>
   * The separator is assumed to contain a <i>single</i> character,
   * although it could also contain multiple characters, each of
   * which acts as a separator. On no account can it contain multiple
   * characters which act as a single separator.
   *
   * Note:<br>
   * This method is static so it can be used by other classes.
   * The separator supplied to this method is not saved.
   *
   * @param path        the pathname to parse
   * @param aSeparator  the directory-name separator --
   *                    "/" for Unix, "\" for DOS, "." for packages.
   */
  public static LinkedList parsePath(String path, String aSeparator) {
      if (path == null || path.equals("")) return null;

     LinkedList pathList = new LinkedList();
     if (path == null || path.equals("")) {
        // Match all paths when the specifier is null
        pathList.add("*");
        return pathList;
     }
     StringTokenizer st = new StringTokenizer(path, aSeparator);
     while (st.hasMoreTokens()) {
       String pattern = st.nextToken();
       pathList.add(pattern);
     }
     return pathList;
  }

  /**
   * Initialize the list of path patterns
   *
   * @param pList     a list of Strings
   * @throws java.lang.ClassCastException if the list entries are not strings
   */
  protected void initPatternList(List pList) {
     // Unfortunately, a List doesn't have getLast()
     // Construct a LinkedList, so we can use that method.
     LinkedList pLL = new LinkedList(pList);

     Iterator it = pLL.iterator();
     while (it.hasNext()) {
        String pattern = (String) it.next();
        if (pattern.equals("?*")) {
           // Handle the special case where the pattern specifies
           // "all directories, but not subdirectories".
           pattern = "*";
        }
        patternList.add(new Specifier(pattern));
     }
     String s = (String) pLL.getLast();
     if (s.equals("*")) {
        // When the last element is "*", the pattern matches
        // all subdirectories as well. Ex: java.awt.*
        openEnded = true;
     }
  }

  /**
   * Check the string argument to see if it matches this specifier,
   * where the default directory-seperator for the current system
   * is used to divide the input string into directory-name segments.
   *
   * @param s     the string to match, where a null or empty string
   *              matches nothing
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
   * @param path   the directory-path string to match,
   *               where a null or empty string matches nothing
   * @param separator  the directory-name separator --
   *                    "/" for Unix, "\" for DOS, "." for packages.
   * @return true if the string matche this specifier
   */
  public boolean match(String path, String separator) {
     if (path == null || path.equals("")) return false;
     LinkedList testList = parsePath(path, separator);
     return match(testList);
  }

  /**
   * Check the list of path-elements to see if it matches this
   * specifier.
   *
   * @param testList   a list
   * @return true if the string matches this specifier
   */
  public boolean match(List testList) {
     Iterator plt = patternList.iterator();
     Iterator tlt = testList.iterator();
     while (plt.hasNext()) {
        if (!tlt.hasNext()) {
           // We ran out of argument elements to check.
           // If this is an openEnded pattern (eg. java.awt.*)
           // and there is exactly one item left in the pattern
           // list, it must be "*", and this is a match.
           // ("java.awt" matches the pattern java.awt.*)
           // Otherwise, no match.
           if (openEnded) {
              Specifier pattern = (Specifier) plt.next();
              if (!plt.hasNext()) return true;
           }
           return false;
        }
        Specifier pattern = (Specifier) plt.next();
        String test = (String) tlt.next();
        if (!pattern.match(test)) {
           // This part of the test-path doesn't match the
           // specifier-pattern.
           return false;
        }
     }
     // The last part of the pattern was matched.
     // If the last entry is "*", return true.
     // Otherwise only return true if the string
     // contents have been exhausted.
     if (openEnded) return true;
     if (tlt.hasNext()) return false;
     return true;
  }

  public String toString() {
      String s = "";
      Iterator pt = patternList.iterator();
      while (pt.hasNext()) {
         Specifier pattern = (Specifier) pt.next();
         if (s != "") s += separator;
         s += pattern.toString();
      }
      return s;
   }

}
