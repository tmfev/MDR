/*
 * @(#)DocCheckConstants.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

/**
 * Constants used in the doccheck app.
 *
 * @author Eric Armstrong
 */
public interface DocCheckConstants {
  static final String badTextColor = "Red";

// ITEM TYPES
// static final int SUMMARY = 0;
// static final int PKG     = 1;
// static final int CLASS   = 2;
// static final int IFACE   = 3;
// static final int FIELD   = 4;
// static final int METHOD  = 5;
// static final int CONSTR  = 6;

  // ERROR CATEGORIES
  static final int MAJOR_ERR      = 0;   
  static final int   PKG_ERR      = 0;  // Missing comment in pkg,
  static final int   TYPE_ERR     = 0;  // class, or interface
  static final int     IFC_ERR    = 0;  
  static final int     CLASS_ERR  = 0;
  /*...*/
  static final int MEMBER_ERR       = 1;  // Missing member comment
  static final int   INNER_ERR      = 1;  // Inner classes
  static final int   FIELD_ERR      = 1;
  static final int   EXECUTABLE_ERR = 1;
  static final int     CONSTR_ERR   = 1;
  static final int     METHOD_ERR   = 1;
  /*...*/
  static final int TAG_ERR    = 2;  // Bad or missing tag
  static final int TEXT_ERR   = 3;  // Error in the text
  static final int WARNING    = 4;  // Something to look for
  /*...*/
  static final int ERR_CATEGORY_COUNT = WARNING + 1;

  // This is the level I would LIKE to assign to missing 
  // comments that can be generated from the method name.
  // static final int SELF_EVIDENT = WARNING;
  //
  // Here is the level we WILL assign to missing comments
  // that can be generated from the method name.
  static final int SELF_EVIDENT = EXECUTABLE_ERR;

  // TAG TYPES
  static final int NONE        = -1;
  static final int TAG_SPACER  =  0;   // Spacer "tag" (" *")
  static final int CLASS_TAG   =  1;   // @author or @version
  static final int METHOD_TAG  =  2;   // @param, @return, @exception, @see
  static final int INVALID_TAG =  3;   // invalid tag
  static final int SEE_TAG     =  4;   // @see tag

  // ERROR MESSAGE TYPES
  static final int NO_COMMENT     = 0;
  static final int NO_TEXT        = NO_COMMENT +1;
//NOT USED
//static final int NO_GENABLE     = NO_TEXT +1;
  static final int NO_SINCE       = NO_TEXT + 1;  // Pkg err
  static final int NO_EVIDENT     = NO_SINCE + 1; // self-evident comment
  /*...*/
  static final int HTML_FIRST     = NO_EVIDENT + 1; // 1st sentence error
  static final int HTML_BODY      = HTML_FIRST + 1;
  static final int HTML_NO_PERIOD = HTML_BODY + 1;
  static final int HTML_LIST      = HTML_NO_PERIOD + 1;
  static final int HTML_ERROR     = HTML_LIST + 1;
  static final int HTML_UNCLOSED  = HTML_ERROR + 1;
  static final int HTML_EXTRA     = HTML_UNCLOSED + 1;
  /*...*/
  static final int DEFAULT_CONSTR = HTML_EXTRA + 1;
  static final int IMPLEMENTS     = DEFAULT_CONSTR + 1;
  static final int OVERRIDES      = IMPLEMENTS + 1;
  /*...*/
  static final int TAG_MISSING    = OVERRIDES + 1;
  static final int TAG_INVALID    = TAG_MISSING + 1;
  static final int TAG_TEXT       = TAG_INVALID + 1;
  static final int TAG_EXTRA      = TAG_TEXT + 1;
  static final int TAG_MULTVER    = TAG_EXTRA + 1;
  /*...*/
  static final int RET_MISSING    = TAG_MULTVER + 1;
  static final int RET_INVALID    = RET_MISSING + 1;
  static final int RET_TEXT       = RET_INVALID + 1;
  static final int RET_EXTRA      = RET_TEXT + 1;
  /*...*/
  static final int EXC_MISSING    = RET_EXTRA + 1;
  static final int EXC_INVALID    = EXC_MISSING + 1;
  static final int EXC_TEXT       = EXC_INVALID + 1;
  static final int EXC_EXTRA      = EXC_TEXT + 1;
  /*...*/
  static final int SEE_MISSING    = EXC_EXTRA + 1;
  static final int SEE_INVALID    = SEE_MISSING + 1;
  static final int SEE_TEXT       = SEE_INVALID + 1;
  /*...*/
  static final int ERR_TYPE_COUNT = SEE_TEXT + 1;
}
