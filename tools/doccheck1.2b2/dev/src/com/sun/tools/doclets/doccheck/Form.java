/*
 * @(#)Form.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */


package com.sun.tools.doclets.doccheck;

import  java.util.*;
import com.sun.tools.doclets.util.HtmlTagWriter;

/**
 * A class that builds a template for a JavaDoc
 * comment that needs to be added or corrected.
 *
 * @author Eric Armstrong
 */
public class Form implements DocCheckConstants {
  private List template = new LinkedList();  // list of template text
  private Stack lastName = new Stack();   // index of last name added. error messages go here.
  private int tagCount = 0;   // #of tags added -- for tag-separator
  private int lastTagCount = 0;  // Did last item printed include tags??
  private int lastTagAdded = NONE;

  /** Create a template-object */
  public Form() {
  }

  /**
   * Print the template.
   */
  public void print(HtmlTagWriter h) {
    if (template.size() == 0) return;
    h.pre();
    for (int i=0; i<template.size(); i++) {
      h.println(template.get(i));
    }
    h.preEnd();
  }

  /** Start of template */
  public void start() {
    template.add("/**");
    tagCount = 0;
  }

  /** End of template */
  public void end() {
    template.add(" */");
  }

  /**
   * Add a line of text.
   *
   * @param s    a String containing the text to add
   */
  public void addLine(String s) {
    template.add(s);
    lastTagAdded = NONE;
  }

  /**
   * Append text to last line.
   *
   * @param s    a String containing the text to add
   */
  public void append(String s) {
    int idx = template.size() - 1;
    if (idx < 0) {
       template.add("BUG: indexing error in Form.append()");
       return;
    }
    template.set(idx, ((String)template.get(idx)) + s);
  }

  /**
   * Add a tag. If this is the first tag added,
   * add a tag separator line.
   *
   * @param tag     a String containing the tag to add
   * @param tagType an int specifying the type of tag
   */
  public void addTag(String tag, int tagType) {
    if ((lastTagAdded != TAG_SPACER) && (tagType != lastTagAdded)) {
      if (tagType == INVALID_TAG) addBadTagSpacer();
      else addTagSpacer();
    }
    template.add(tag);
    tagCount++;
    lastTagAdded = tagType;
  }

  /**
   * Add a tag spacer. Its like adding a tag, except that
   * there is no message. It contributes to the tag count, though,
   * so that only one separator is created.
   *
   */
  public void addTagSpacer() {
    template.add(" *");
    tagCount = 0;
    lastTagAdded = TAG_SPACER;
  }
  /**
   * Add a bad tag spacer.
   *
   */
  void addBadTagSpacer() {
    template.add(badText(" ?"));
    lastTagAdded = TAG_SPACER;
  }
  /** Return a string wrapped in the bad text color */
  public static String badText(String s) {
     return "<b><font color=\""+ badTextColor +"\">"
            + s + "</font></b>";
  }

}//Form

