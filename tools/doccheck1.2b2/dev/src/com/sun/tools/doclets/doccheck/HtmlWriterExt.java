/*
 * @(#)HtmlWriterExt.java	1.3  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.doccheck;

import java.io.*;
import com.sun.tools.doclets.StandardDoclet1_3_HtmlWriter;

/**
 * An extension of HtmlWriter to add a alignable column-spanning
 * method.
 *
 * @author Eric Armstrong
 */
public class HtmlWriterExt extends StandardDoclet1_3_HtmlWriter
{
    public HtmlWriterExt(String filename, String docencoding)
                      throws IOException, UnsupportedEncodingException {
        super(filename, docencoding);
    }

    /**
     * Print &lt;TD ALIGN="align" COLSPAN=rowspan&gt; tag.
     *
     * @param align    String align.
     * @param colspan  integer colspan.
     */
    public void tdAlignColspan(String align, int colspan) {
        print("<TD ALIGN=\"" + align + "\" COLSPAN=" + colspan + ">");
    }

    /**
     * Print &lt;TD ALIGN="align" WIDTH="width"&gt; tag.
     *
     * @param align   String horizontal align.
     * @param valign  String vertical align.
     * @param width   String width.
     */
    public void tdAlignValignWidth(String align, String valign, String width) {
        print("<TD ALIGN=\"" + align
          + "\" VALIGN=\"" + valign
          + "\" WIDTH=\"" + width
          + "\">");
    }

    /**
     * Print &lt;a HREF="href"&gt; text &lt;/a&gt; tag.
     *
     * @param href  The link to go to.
     * @param text  The string to display.
     */
    public void link(String href, String text) {
        print("<a href=\"" + href + "\">" + text + "</a>");
    }

    /**
     * Print numeric table data, right aligned.
     *
     * @param n    Integer colspan.
     */
    public void dat(int n) {
       if (n==0) dat("--");
          else dat(""+n);
    }

    /**
     * Print percentage table data, right aligned.
     */
    public void datp(long n) {
       if (n==0) dat("--");
          else dat(""+n+"%");
    }

    /**
     * Print percentage table data, right aligned.
     */
    public void datp(float n) {
       if (n==0) dat("--");
          else dat(""+n+"%");
    }

    /**
     * Print table data, right aligned.
     *
     * @param s    String align.
     */
    public void dat(String s) {
       tdAlign("Right");
       print(s);
       tdEnd();
    }
}
