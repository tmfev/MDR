/*
 * @(#)Specifier.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.util;

import java.util.*;

/**
 * Foundation class for package and filename matching.
 * Possible patterns include:
 * <pre>
 *    s       -- equals s
 *    s*      -- starts with s
 *    *s      -- ends with s
 *    *s*     -- contains s
 *    s1*s2   -- starts with s1, ends with s2
 *    s1*s2*  -- starts with s1, contains s2
 *    *s1*s2* -- contains s1 and s2
 *    etc.
 * </pre>
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
 * @see PathSpecifier
 * @see FileSpecifier
 * @author Eric Armstrong
 */
public class Specifier {
   protected boolean equals;
   protected boolean startsWith;
   protected boolean endsWith;
   protected boolean contains;

   String startString = "";
   String endString = "";
   LinkedList containsStrings = new LinkedList();

   // No construction without a pattern to match
   private Specifier() { }

   /**
    * Create a specifier object, supplying a pattern
    * to match.
    */
   public Specifier(String pattern) {
      if (pattern == null || pattern.equals("")) {
         // match() function will always return true.
         return;
      }
      LinkedList element = new LinkedList();
      StringTokenizer t = new StringTokenizer(pattern, "*", true);
      while (t.hasMoreElements()) {
         element.add(t.nextElement());
      }
      if (element.size() == 1) {
         String s = (String) element.getFirst();
         if (s.equals("*")) {
            // Do nothing. match() will always return true.
            return;
         }
         // Real text
         equals = true;
         startString = (String) element.getFirst();
         return;
      }
      // Multiple elements in the pattern
      // Pick off start and end strings.
      // Add remaining items to the list of contains strings.
      if (!element.getFirst().equals("*")) {
         startsWith = true;
         startString = (String) element.getFirst();
         element.removeFirst();
      }
      if (!element.getLast().equals("*")) {
         endsWith = true;
         endString = (String) element.getLast();
         element.removeLast();
      }
      ListIterator plt = element.listIterator();
      while (plt.hasNext()) {
        String x = (String) plt.next();
        if (x.equals("*")) continue;
        containsStrings.add(x);
      }
   }

   /**
    * Return true if the argument is a match for this
    * specifier.
    *
    * @param s  The string to match, where a null or empty
    *           string matches nothing
    */
   public boolean match(String s) {
     boolean trace = false;
     if (trace) System.out.println("");
     if (trace) System.out.print("Matching " +s + " to pattern: "
                                + this.toString());
     if (s == null || s.equals("")) return false;
     if (equals     && !s.equals(startString)) return false;
     if (startsWith && !s.startsWith(startString)) return false;
     if (endsWith   && !s.endsWith(endString)) return false;
     if (contains) {
        ListIterator pLit = containsStrings.listIterator();
        while (pLit.hasNext()) {
           String pattern = (String) pLit.next();
           int idx = s.indexOf(pattern);
           if (idx == -1) return false;
           if (idx + pattern.length() == s.length()) {
              // Match occured at the end of the string.
              // In that case, the substring assignment below
              // would fail. Continue the loop. If there are
              // more items to match, the next test will fail.
              // Otherwise, the match has succeeded.
              s = "";
              continue;
           }
           s = s.substring(idx + pattern.length());
        }
     }
     if (trace) System.out.print(" good match");
     return true;
   }

   public String toString() {
      if (equals) return startString;

      // If there is a startString, append "*" to it.
      // (There must be a follow up, or this would be an equals match.)
      // If startString is null, the remainder of the pattern is
      // an endsWith or contains. In either case, it must start
      // with "*".
      String s = startString;
      s += "*";

      Iterator st = containsStrings.iterator();
      while (st.hasNext()) {
         s += (String) st.next() + "*";
      }
      s += endString;
      return s;
   }
}

