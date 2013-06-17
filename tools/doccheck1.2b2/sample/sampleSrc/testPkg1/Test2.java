/*
 * Copyright 2003 Sun Microsystems, Inc. ALL RIGHTS RESERVED. 
 * Use of this software is authorized pursuant to the terms of the 
 * license found at http://developer.java.sun.com/berkeley_license.html
 */

package testPkg1;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.URL;
/**
 * Class with a comment, no author tag, and version tag missing text.
 * @version
 */
public class Test2 implements ActionListener
{
   protected int aVar;  // A variable with no comment

   /** A valid constructor. */
   public Test2() {
   }

   // A method that implements an interface
   public void actionPerformed(ActionEvent e) {
   }

   // A method with no comment.
   public static void noComment()
   { }

   // A method with no comment that needs one tag.
   public static void noCommentWithTag(int i)
   { }

   /**
    * @return a String for a method with no text
    */
   public static String getText()
   { }

   /**
    * A method with no param tags.
    */
   public static void noParamTags(boolean b, int i, String s, Test2 t, Object o)
   { }

   // Getter.
   public static int getFoo()
   { return 1; }

   // Setter.
   public static void setFoo(int f)
   { }

   // Tester.
   public static boolean isFoo()
   { return true; }

   /**
    * Tag errors.
    * //@param missing an int
    * @param wrongType a boolean, he said, wrongly
    * @param noTagComment
    */
   public static boolean tagErrors(int missing, int wrongType, int noTagComment)
   { return true; }
   /**
    * A method with no return tag.
    */
   public static boolean noReturnTag()
   { return true; }

   // An inner class with no comment
   public class InnerClass {
     // An inner class constructor with no comment.
     // It has an arg so it won't be confused with the default constructor.
     public InnerClass(int i) { }

     // An inner class method with no comment
     public void innerMethod() {
     }
   }
   public interface InnerInterface {
     // An interface method with no comment
     public void interfaceMethod();
   }

   // Overridden method.
   public String toString()
   { return "Test"; }

   // Array values in set/get methods
   public void setArgumentArray(String[] arr) { }
   public String[] getArgumentArray(int index) {return null;}

   public boolean isReallyGood() { return true; }
   public void setReallyGood(boolean yes) { }

   /**
    * This first sentence has no period
    */
   public void noPeriodInFirstSentence() { }

   /**
    * The first sentence of this comment contains
    * <ul>
    * <li>An unterminated html tag.
    * <li>A series of list tags.
    * <li>All of the above.
    * </ul>
    * (The first sentence goes into the index, where
    * unterminated html tags create problems.)
    */
   public void unterminatedHTML_1() { }

   /**
    * The second sentence of this comment contains
    * unterminated html. This <b>bold<b> word is not
    * right!
    */
   public void unterminatedHTML_2() { }

   /**
    * The first sentence of this comment contains
    * <ul>
    * <li>A list
    * <li>With no periods
    * </ul>
    * So the first "sentence" is everything up to here.
    */
   public void listInFirstSentence() { }

   /**
    * Missing @see tag.
    * @return an int
    */
   public int getValue() { }

   /**
    * Missing @see tag.
    */
   public void addFooListener(Test2 x) { }

   /**
    * Missing @see tag.
    */
   public void removeFooListener(Test2 x) { }

   /**
    * Missing @see tag.
    * //@param v an int
    */
   public void setValue(int v) { }

   // Method with only an @see tag
   /** @see Test2#findIndex */
   public void delegationMethod() { 
     // Delegate
     Test2.findIndex();
   }

   // Method with only an @return tag
   /** @return the current index */
   public int findIndex() { }
//-----------------------------------------------------------------------------
   // Everything after this checks to make sure that errors
   // DON'T appear where they are not wanted.
   public void THIS_IS_THE_LAST_METHOD_ERROR()
   {  }

   /**
    * Some folks make links for their types!
    * (There should be no @see tag to setURL, because there is
    * no setURL method.)
    *
    * @return the <a href="http://very/strange">URL</a> tag to test
    */
   public URL getURL() {
     return new URL();
   }

   /**
    * A method that returns an array. (no error)
    * @return a String[]
    */
   public String[] returnsFullArrayName()
   { return true; }

   /**
    * A method that returns an array. (no error)
    * @return an array of String objects
    */
   public String[] returnsSimpleArrayName()
   { return true; }

   /**
    * A method that returns an array. (no error)
    * @return an array of Strings
    */
   public String[] returnsPluralArrayName()
   { return true; }
} //Test
