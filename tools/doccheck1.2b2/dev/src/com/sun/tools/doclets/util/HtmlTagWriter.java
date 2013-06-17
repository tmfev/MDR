/*
 * @(#)HtmlTagWriter.java	1.3  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.util;

import com.sun.javadoc.*;
import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Class for the Html format code generation.
 * Initilizes PrintWriter with FileWriter, to enable print
 * related methods to generate the code to the named File through FileWriter.
 *
 * @since JDK1.2
 * @author Atul M Dambalkar
 */
public class HtmlTagWriter extends PrintWriter {

    /**
     * Name of the file to which this writer is writing.
     */
    protected final String htmlFilename;

    /**
     * URL file separator string("/").
     */
    public static final String fileseparator = "/";

    /**
     * Initializes PrintWriter with the FileWriter.
     *
     * @param filename     File Name to which the PrintWriter will do the Output.
     * @param docencoding  Encoding to be used for this file.
     * @exception IOException Exception raised by the FileWriter is passed on
     * to next level.
     * @exception UnSupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     */
    public HtmlTagWriter(String filename, String docencoding)
                      throws IOException, UnsupportedEncodingException {
        super(genWriter(null, filename, docencoding));
        htmlFilename = filename;
    }

    /**
     * Initializes PrintWriter with the FileWriter.
     *
     * @param path         The directory path to be created for this file.
     * @param filename     File Name to which the PrintWriter will do the Output.
     * @param docencoding  Encoding to be used for this file.
     * @exception IOException Exception raised by the FileWriter is passed on
     * to next level.
     * @exception UnSupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     */
    public HtmlTagWriter(String path, String filename, String docencoding)
                      throws IOException, UnsupportedEncodingException {
        super(genWriter(path, filename, docencoding));
        htmlFilename = filename;
    }

    /**
     * Create the directory path for the file to be generated, construct
     * FileOutputStream and OutputStreamWriter depending upon docencoding.
     *
     * @param path         The directory path to be created for this file.
     * @param filename     File Name to which the PrintWriter will do the Output.
     * @param docencoding  Encoding to be used for this file.
     * @exception IOException Exception raised by the FileWriter is passed on
     * to next level.
     * @exception UnSupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     * @return Writer Writer for the file getting generated.
     * @see java.io.FileOutputStream
     * @see java.io.OutputStreamWriter
     */
    public static Writer genWriter(String path, String filename,
                                   String docencoding)
                            throws IOException, UnsupportedEncodingException {
        FileOutputStream fos;
        if (path != null) {
          //DirectoryManager.createDirectory(path);
          File dir = new File(path);
          if (!dir.exists()) dir.createNewFile();
          fos = new FileOutputStream(((path.length() > 0)?
                                       path + File.separator: "") + filename);
        } else {
            fos = new FileOutputStream(filename);
        }
        if (docencoding == null) {
            OutputStreamWriter oswriter = new OutputStreamWriter(fos);
            docencoding = oswriter.getEncoding();
            return oswriter;
        } else {
            return new OutputStreamWriter(fos, docencoding);
        }
    }

    /**
     * Print &lt;HTML&gt; tag. Add a newline character at the end.
     */
    public void html() {
        println("<HTML>");
    }

    /**
     * Print &lt;/HTML&gt; tag. Add a newline character at the end.
     */
    public void htmlEnd() {
        println("</HTML>");
    }

    /**
     * Print &lt;BODY&gt; tag. Add a newline character at the end.
     */
    public void body() {
        println("<BODY>");
    }

    /**
     * Print &lt;BODY BGCOLOR="bgcolor"&gt; tag. Add a newline character at the end.
     *
     * @param bgcolor BackGroung color.
     */
    public void body(String bgcolor) {
        println("<BODY BGCOLOR=\"" + bgcolor + "\">");
    }

    /**
     * Print &lt;/BODY&gt; tag. Add a newline character at the end.
     */
    public void bodyEnd() {
        println("</BODY>");
    }

    /**
     * Print &lt;TITLE&gt; tag. Add a newline character at the end.
     */
    public void title() {
        println("<TITLE>");
    }

    /**
     * Print &lt;/TITLE&gt; tag. Add a newline character at the end.
     */
    public void titleEnd() {
        println("</TITLE>");
    }

    /**
     * Print &lt;UL&gt; tag. Add a newline character at the end.
     */
    public void ul() {
        println("<UL>");
    }

    /**
     * Print &lt;/UL&gt; tag. Add a newline character at the end.
     */
    public void ulEnd() {
        println("</UL>");
    }

    /**
     * Print &lt;LI&gt; tag.
     */
    public void li() {
        print("<LI>");
    }

    /**
     * Print &lt;LI TYPE="type"&gt; tag.
     *
     * @param type Type string.
     */
    public void li(String type) {
        print("<LI TYPE=\"" + type + "\">");
    }

    /**
     * Print &lt;H1&gt; tag. Add a newline character at the end.
     */
    public void h1() {
        println("<H1>");
    }

    /**
     * Print &lt;/H1&gt; tag. Add a newline character at the end.
     */
    public void h1End() {
        println("</H1>");
    }

    /**
     * Print text with &lt;H1&gt; tag. Also adds &lt;/H1&gt; tag. Add a newline character
     * at the end of the text.
     *
     * @param text Text to be printed with &lt;H1&gt; format.
     */
    public void h1(String text) {
        h1();
        println(text);
        h1End();
    }

    /**
     * Print &lt;H2&gt; tag. Add a newline character at the end.
     */
    public void h2() {
        println("<H2>");
    }

    /**
     * Print text with &lt;H2&gt; tag. Also adds &lt;/H2&gt; tag. Add a newline character
     *  at the end of the text.
     *
     * @param text Text to be printed with &lt;H2&gt; format.
     */
    public void h2(String text) {
        h2();
        println(text);
        h2End();
    }

    /**
     * Print &lt;/H2&gt; tag. Add a newline character at the end.
     */
    public void h2End() {
        println("</H2>");
    }

    /**
     * Print &lt;H3&gt; tag. Add a newline character at the end.
     */
    public void h3() {
        println("<H3>");
    }

    /**
     * Print text with &lt;H3&gt; tag. Also adds &lt;/H3&gt; tag. Add a newline character
     *  at the end of the text.
     *
     * @param text Text to be printed with &lt;H3&gt; format.
     */
    public void h3(String text) {
        h3();
        println(text);
        h3End();
    }

    /**
     * Print &lt;/H3&gt; tag. Add a newline character at the end.
     */
    public void h3End() {
        println("</H3>");
    }

    /**
     * Print &lt;H4&gt; tag. Add a newline character at the end.
     */
    public void h4() {
        println("<H4>");
    }

    /**
     * Print &lt;/H4&gt; tag. Add a newline character at the end.
     */
    public void h4End() {
        println("</H4>");
    }

    /**
     * Print text with &lt;H4&gt; tag. Also adds &lt;/H4&gt; tag. Add a newline character
     * at the end of the text.
     *
     * @param text Text to be printed with &lt;H4&gt; format.
     */
    public void h4(String text) {
        h4();
        println(text);
        h4End();
    }

    /**
     * Print &lt;H5&gt; tag. Add a newline character at the end.
     */
    public void h5() {
        println("<H5>");
    }

    /**
     * Print &lt;/H5&gt; tag. Add a newline character at the end.
     */
    public void h5End() {
        println("</H5>");
    }

    /**
     * Print HTML &lt;IMG SRC="imggif" WIDTH="width" HEIGHT="height" ALT="imgname&gt;
     * tag. It prepends the "images" directory name to the "imggif". This
     * method is used for oneone format generation. Add a newline character
     * at the end.
     *
     * @param imggif   Image GIF file.
     * @param imgname  Image name.
     * @param width    Width of the image.
     * @param height   Height of the image.
     */
    public void img(String imggif, String imgname, int width, int height) {
        println("<IMG SRC=\"images/" + imggif + ".gif\""
              + " WIDTH=\"" + width + "\" HEIGHT=\"" + height
              + "\" ALT=\"" + imgname + "\">");
    }

    /**
     * Print &lt;MENU&gt; tag. Add a newline character at the end.
     */
    public void menu() {
        println("<MENU>");
    }

    /**
     * Print &lt;/MENU&gt; tag. Add a newline character at the end.
     */
    public void menuEnd() {
        println("</MENU>");
    }

    /**
     * Print &lt;PRE&gt; tag. Add a newline character at the end.
     */
    public void pre() {
        println("<PRE>");
    }

    /**
     * Print &lt;/PRE&gt; tag. Add a newline character at the end.
     */
    public void preEnd() {
        println("</PRE>");
    }
    
    /**
     * Print &lt;HR&gt; tag. Add a newline character at the end.
     */
    public void hr() {
        println("<HR>");
    }

    /**
     * Print &lt;HR SIZE="size" WIDTH="widthpercent%"&gt; tag. Add a newline
     * character at the end.
     *
     * @param size           Size of the ruler.
     * @param widthPercent   Percentage Width of the ruler
     */
    public void hr(int size, int widthPercent) {
        println("<HR SIZE=\"" + size + "\" WIDTH=\"" + widthPercent + "%\">");
    }

    /**
     * Print &lt;HR SIZE="size" NOSHADE&gt; tag. Add a newline character at the end.
     *
     * @param size           Size of the ruler.
     * @param noshade        noshade string.
     */
    public void hr(int size, String noshade) {
        println("<HR SIZE=\"" + size + "\" NOSHADE>");
    }

    /**
     * Get the "&lt;B&gt;" string.
     *
     * @return String Return String "&lt;B&gt;";
     */
    public String getBold() {
        return "<B>";
    }

    /**
     * Get the "&lt;/B&gt;" string.
     *
     * @return String Return String "&lt;/B&gt;";
     */
    public String getBoldEnd() {
        return "</B>";
    }

    /**
     * Print &lt;B&gt; tag.
     */
    public void bold() {
        print("<B>");
    }

    /**
     * Print &lt;/B&gt; tag.
     */
    public void boldEnd() {
        print("</B>");
    }

    /**
     * Print text passed, in bold format using &lt;B&gt; and &lt;/B&gt; tags.
     *
     * @param text String to be printed in between &lt;B&gt; and &lt;/B&gt; tags.
     */
    public void bold(String text) {
        bold();
        print(text);
        boldEnd();
    }
    
    /**
     * Print text passed, in Italics using &lt;I&gt; and &lt;/I&gt; tags.
     *
     * @param text String to be printed in between &lt;I&gt; and &lt;/I&gt; tags.
     */
    public void italics(String text) {
        print("<I>");
        print(text);
        println("</I>");
    }

    /**
     * Return, text passed, with Italics &lt;I&gt; and &lt;/I&gt; tags, surrounding it.
     * So if the text passed is "Hi", then string returned will be "&lt;I&gt;Hi&lt;/I&gt;".
     *
     * @param text String to be printed in between &lt;I&gt; and &lt;/I&gt; tags.
     */
    public String italicsText(String text) {
        return "<I>" + text + "</I>";
    }

    public String codeText(String text) {
        return "<CODE>" + text + "</CODE>";
    }

    /**
     * Print "&#38;nbsp;", non-breaking space.
     */
    public void space() {
        print("&nbsp;");
    }

    /**
     * Print &lt;DL&gt; tag. Add a newline character at the end.
     */
    public void dl() {
        println("<DL>");
    }

    /**
     * Print &lt;/DL&gt; tag. Add a newline character at the end.
     */
    public void dlEnd() {
        println("</DL>");
    }

    /**
     * Print &lt;DT&gt; tag.
     */
    public void dt() {
        print("<DT>");
    }

    /**
     * Print &lt;DT&gt; tag.
     */
    public void dd() {
        print("<DD>");
    }

    /**
     * Print &lt;/DD&gt; tag. Add a newline character at the end.
     */
    public void ddEnd() {
        println("</DD>");
    }

    /**
     * Print &lt;SUP&gt; tag. Add a newline character at the end.
     */
    public void sup() {
        println("<SUP>");
    }

    /**
     * Print &lt;/SUP&gt; tag. Add a newline character at the end.
     */
    public void supEnd() {
        println("</SUP>");
    }
    
    /**
     * Print &lt;FONT SIZE="size"&gt; tag. Add a newline character at the end.
     *
     * @param size String size.
     */
    public void font(String size) {
        println("<FONT SIZE=\"" + size + "\">");
    }

    /**
     * Print &lt;FONT CLASS="stylename"&gt; tag. Add a newline character at the end.
     *
     * @param stylename String stylename.
     */
    public void fontStyle(String stylename) {
        print("<FONT CLASS=\"" + stylename + "\">");
    }
   
    /**
     * Print &lt;FONT SIZE="size" CLASS="stylename"&gt; tag. Add a newline character
     * at the end.
     *
     * @param size String size.
     * @param stylename String stylename.
     */
    public void fontSizeStyle(String size, String stylename) {
        println("<FONT size=\"" + size + "\" CLASS=\"" + stylename + "\">");
    }
    
    /**
     * Print &lt;/FONT&gt; tag.
     */
    public void fontEnd() {
        print("</FONT>");
    }
   
    /**
     * Get the "&lt;FONT COLOR="color"&gt;" string.
     *
     * @param color String color.
     * @return String Return String "&lt;FONT COLOR="color"&gt;".
     */
    public String getFontColor(String color) {
        return "<FONT COLOR=\"" + color + "\">";
    }

    /**
     * Get the "&lt;/FONT&gt;" string.
     *
     * @return String Return String "&lt;/FONT&gt;";
     */
    public String getFontEnd() {
        return "</FONT>";
    }

    /**
     * Print &lt;CENTER&gt; tag. Add a newline character at the end.
     */
    public void center() {
        println("<CENTER>");
    }

    /**
     * Print &lt;/CENTER&gt; tag. Add a newline character at the end.
     */
    public void centerEnd() {
        println("</CENTER>");
    }
    
    /**
     * Print anchor &lt;A NAME="name"&gt; tag.
     *
     * @param name Name String.
     */
    public void aName(String name) {
        print("<A NAME=\"" + name + "\">");
    }

    /**
     * Print &lt;/A&gt; tag.
     */
    public void aEnd() {
        print("</A>");
    }

    /**
     * Print &lt;I&gt; tag.
     */
    public void italic() {
        print("<I>");
    }

    /**
     * Print &lt;/I&gt; tag.
     */
    public void italicEnd() {
        print("</I>");
    }

    /**
     * Print contents within anchor &lt;A NAME="name"&gt; tags.
     *
     * @param name String name.
     * @param content String contents.
     */
    public void anchor(String name, String content) {
        aName(name);
        print(content);
        aEnd();
    }

    /**
     * Print anchor &lt;A NAME="name"&gt; and &lt;/A&gt;tags. Print comment string
     * "&lt;!-- --&gt;" within those tags.
     *
     * @param name String name.
     */
    public void anchor(String name) {
        aName(name);
        print("<!-- -->");
        aEnd();
    }

    /**
     * Print newline and then print &lt;P&gt; tag. Add a newline character at the
     * end.
     */
    public void p() {
        println();
        println("<P>");
    }

    /**
     * Print newline and then print &lt;BR&gt; tag. Add a newline character at the
     * end.
     */
    public void br() {
        println();
        println("<BR>");
    }

    /**
     * Print &lt;ADDRESS&gt; tag. Add a newline character at the end.
     */
    public void address() {
        println("<ADDRESS>");
    }

    /**
     * Print &lt;/ADDRESS&gt; tag. Add a newline character at the end.
     */
    public void addressEnd() {
        println("</ADDRESS>");
    }

    /**
     * Print &lt;HEAD&gt; tag. Add a newline character at the end.
     */
    public void head() {
        println("<HEAD>");
    }

    /**
     * Print &lt;/HEAD&gt; tag. Add a newline character at the end.
     */
    public void headEnd() {
        println("</HEAD>");
    }

    /**
     * Print &lt;CODE&gt; tag.
     */
    public void code() {
        print("<CODE>");
    }

    /**
     * Print &lt;/CODE&gt; tag.
     */
    public void codeEnd() {
        print("</CODE>");
    }
    
    /**
     * Print &lt;EM&gt; tag. Add a newline character at the end.
     */
    public void em() {
        println("<EM>");
    }

    /**
     * Print &lt;/EM&gt; tag. Add a newline character at the end.
     */
    public void emEnd() {
        println("</EM>");
    }

    /**
     * Print HTML &lt;TABLE BORDER="border" WIDTH="width"
     * CELLPADDING="cellpadding" CELLSPACING="cellspacing"&gt; tag.
     *
     * @param border       Border size.
     * @param width        Width of the table.
     * @param cellpadding  Cellpadding for the table cells.
     * @param cellspacing  Cellspacing for the table cells.
     */
    public void table(int border, String width, int cellpadding,
                      int cellspacing) {
        println("\n<TABLE BORDER=\"" + border +
                "\" WIDTH=\"" + width +
                "\" CELLPADDING=\"" + cellpadding +
                "\" CELLSPACING=\"" + cellspacing + "\">");
    }
 
    /**
     * Print HTML &lt;TABLE BORDER="border" CELLPADDING="cellpadding"
     * CELLSPACING="cellspacing"&gt; tag.
     *
     * @param border       Border size.
     * @param cellpadding  Cellpadding for the table cells.
     * @param cellspacing  Cellspacing for the table cells.
     */
    public void table(int border, int cellpadding, int cellspacing) {
        println("\n<TABLE BORDER=\"" + border +
                "\" CELLPADDING=\"" + cellpadding +
                "\" CELLSPACING=\"" + cellspacing + "\">");
    }

    /**
     * Print HTML &lt;TABLE BORDER="border" WIDTH="width"&gt;
     *
     * @param border       Border size.
     * @param width        Width of the table.
     */
    public void table(int border, String width) {
        println("\n<TABLE BORDER=\"" + border +
                "\" WIDTH=\"" + width + "\">");
    }

    /**
     * Print the HTML table tag with border size 0 and width 100%.
     */
    public void table() {
        table(0, "100%");
    }
    
    /**
     * Print &lt;/TABLE&gt; tag. Add a newline character at the end.
     */
    public void tableEnd() {
        println("</TABLE>");
    }

    /**
     * Print &lt;TR&gt; tag. Add a newline character at the end.
     */
    public void tr() {
        println("<TR>");
    }

    /**
     * Print &lt;/TR&gt; tag. Add a newline character at the end.
     */
    public void trEnd() {
        println("</TR>");
    }
   
    /**
     * Print &lt;TD&gt; tag.
     */
    public void td() {
        print("<TD>");
    }

    /**
     * Print &lt;TD NOWRAP&gt; tag.
     */
    public void tdNowrap() {
        print("<TD NOWRAP>");
    }

    /**
     * Print &lt;TD WIDTH="width"&gt; tag.
     *
     * @param width String width.
     */
    public void tdWidth(String width) {
        print("<TD WIDTH=\"" + width + "\">");
    }

    /**
     * Print &lt;/TD&gt; tag. Add a newline character at the end.
     */
    public void tdEnd() {
        println("</TD>");
    }

    /**
     * Print &lt;LINK str&gt; tag.
     *
     * @param str String.
     */
    public void link(String str) {
        println("<LINK " + str + ">");
    }

    /**
     * Print "&lt;!-- " comment start string.
     */
    public void commentStart() {
         print("<!-- ");
    }
 
    /**
     * Print "--&gt;" comment end string. Add a newline character at the end.
     */
    public void commentEnd() {
         println("-->");
    }

    /**
     * Print &lt;TR BGCOLOR="color" CLASS="stylename"&gt; tag. Adds a newline character
     * at the end.
     *
     * @param color String color.
     * @param stylename String stylename.
     */
    public void trBgcolorStyle(String color, String stylename) {
        println("<TR BGCOLOR=\"" + color + "\" CLASS=\"" + stylename + "\">");
    }

    /**
     * Print &lt;TR BGCOLOR="color"&gt; tag. Adds a newline character at the end.
     *
     * @param color String color.
     */
    public void trBgcolor(String color) {
        println("<TR BGCOLOR=\"" + color + "\">");
    }

    /**
     * Print &lt;TR ALIGN="align" VALIGN="valign"&gt; tag. Adds a newline character
     * at the end.
     *
     * @param align String align.
     * @param valign String valign.
     */
    public void trAlignVAlign(String align, String valign) {
        println("<TR ALIGN=\"" + align + "\" VALIGN=\"" + valign + "\">");
    }

    /**
     * Print &lt;TD COLSPAN=i&gt; tag.
     *
     * @param i integer.
     */
    public void tdColspan(int i) {
        print("<TD COLSPAN=" + i + ">");
    }

    /**
     * Print &lt;TD BGCOLOR="color" CLASS="stylename"&gt; tag.
     *
     * @param color String color.
     * @param stylename String stylename.
     */
    public void tdBgcolorStyle(String color, String stylename) {
        print("<TD BGCOLOR=\"" + color + "\" CLASS=\"" + stylename + "\">");
    }
 
    /**
     * Print &lt;TD COLSPAN=i BGCOLOR="color" CLASS="stylename"&gt; tag.
     *
     * @param i integer.
     * @param color String color.
     * @param stylename String stylename.
     */
    public void tdColspanBgcolorStyle(int i, String color, String stylename) {
        print("<TD COLSPAN=" + i + " BGCOLOR=\"" + color + "\" CLASS=\"" +
              stylename + "\">");
    }

    /**
     * Print &lt;TD ALIGN="align"&gt; tag. Adds a newline character
     * at the end.
     *
     * @param align String align.
     */
    public void tdAlign(String align) {
        print("<TD ALIGN=\"" + align + "\">");
    }

    /**
     * Print &lt;TD ALIGN="align" CLASS="stylename"&gt; tag.
     *
     * @param align        String align.
     * @param stylename    String stylename.
     */
    public void tdVAlignClass(String align, String stylename) {
        print("<TD VALIGN=\"" + align + "\" CLASS=\"" + stylename + "\">");
    }

    /**
     * Print &lt;TD VALIGN="valign"&gt; tag.
     *
     * @param valign String valign.
     */
    public void tdVAlign(String valign) {
        print("<TD VALIGN=\"" + valign + "\">");
    }

    /**
     * Print &lt;TD ALIGN="align" VALIGN="valign"&gt; tag.
     *
     * @param align   String align.
     * @param valign  String valign.
     */
    public void tdAlignVAlign(String align, String valign) {
        print("<TD ALIGN=\"" + align + "\" VALIGN=\"" + valign + "\">");
    }

    /**
     * Print &lt;TD ALIGN="align" ROWSPAN=rowspan&gt; tag.
     *
     * @param align    String align.
     * @param rowspan  integer rowspan.
     */
    public void tdAlignRowspan(String align, int rowspan) {
        print("<TD ALIGN=\"" + align + "\" ROWSPAN=" + rowspan + ">");
    }

    /**
     * Print &lt;TD ALIGN="align" VALIGN="valign" ROWSPAN=rowspan&gt; tag.
     *
     * @param align    String align.
     * @param valign  String valign.
     * @param rowspan  integer rowspan.
     */
    public void tdAlignVAlignRowspan(String align, String valign,
                                     int rowspan) {
        print("<TD ALIGN=\"" + align + "\" VALIGN=\"" + valign
                + "\" ROWSPAN=" + rowspan + ">");
    }

    /**
     * Print &lt;BLOCKQUOTE&gt; tag. Add a newline character at the end.
     */
    public void blockquote() {
        println("<BLOCKQUOTE>");
    }

    /**
     * Print &lt;/BLOCKQUOTE&gt; tag. Add a newline character at the end.
     */
    public void blockquoteEnd() {
        println("</BLOCKQUOTE>");
    }

    /**
     * Get the "&lt;CODE&gt;" string.
     *
     * @return String Return String "&lt;CODE>";
     */
    public String getCode() {
        return "<CODE>";
    }

    /**
     * Get the "&lt;/CODE&gt;" string.
     *
     * @return String Return String "&lt;/CODE&gt;";
     */
    public String getCodeEnd() {
        return "</CODE>";
    }
 
    /**
     * Print &lt;NOFRAMES&gt; tag. Add a newline character at the end.
     */
    public void noFrames() {
        println("<NOFRAMES>");
    }

    /**
     * Print &lt;/NOFRAMES&gt; tag. Add a newline character at the end.
     */
    public void noFramesEnd() {
        println("</NOFRAMES>");
    }
  /**
   * Print &lt;TD ALIGN="align" COLSPAN=rowspan&gt; tag.
   *
   * @param align    String align.
   * @param colspan  integer colspan.
   */
  public void tdAlignColspan(String align, int colspan) {
    print("\n<TD ALIGN=\"" + align + "\" COLSPAN=" + colspan + ">");
  }
  
  /**
   * Print &lt;TD ALIGN="align" WIDTH="width"&gt; tag.
   *
   * @param align    String horizontal align.
   * @param valign   String vertical align.
   * @param width    String width.
   */
  public void tdAlignValignWidth(String align, String valign, String width) {
    print("\n<TD ALIGN=\"" + align
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
    print("\n<a href=\"" + href + "\">" + text + "</a>");
  }
  
  /**
   * Print numeric table data, right aligned.
   *
   * @param n  integer colspan.
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
