/*
 * @(#)PackageList.java	1.2  03/07/09
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.sun.tools.doclets.util;

import java.io.File;
import java.util.*;

/**
 * Generates a list of packages under the specified root directory(s).
 * Writes the list to standard output when run from the command line,
 * or returns it as a LinkedList when the {@link #findPkgs} method is
 * invoked.
 *
 * @author Eric Armstrong
 */
public class PackageList {
  /**
   * A list of directory names to be skipped anywhere they appear in the
   * hierarchy. Use simple names and invoke "namesToSkip.add(new Specifier(name))"
   * to add entries to this list, or use the {@link #processArgs} method
   * to do all the setup work.
   *
   * @see Specifier
   */
  public static LinkedList namesToSkip = new LinkedList();
  /**
   * A list of fully-qualified directory paths to be skipped. Use fully qualified
   * package names and invoke "pkgsToSkip.add(new PathSpecifier(pkg, "."))"
   * to add entries to this list, or use the {@link #processArgs} method
   * to do all the setup work.
   *
   * @see PathSpecifier
   */
  public static LinkedList pkgsToSkip  = new LinkedList();

  static LinkedList rootPaths   = new LinkedList();
  static LinkedList pkgList     = new LinkedList();

  /**
   * Recursively searches the list of directories specified on the command line
   * and identifies Java packages. For complete usage instructions, see
   * {@link #echoUsage} or run the program with no arguments.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      echoUsage();
      return;
    }
    processArgs(args);
    ListIterator paths = rootPaths.listIterator();
    while (paths.hasNext()) {
      String rootPath = (String) paths.next();
      File root = new File(rootPath);
      if (root.isDirectory()) {
         findPkgs(root, root);
      }
    }
    // Write package names
    ListIterator plt = pkgList.listIterator();
    while (plt.hasNext()) {
       String pkgName = (String) plt.next();
       System.out.println(pkgName);
    }
  }//main
  
  /**
   * Recursively searches a directory and returns a list of package
   * names corresponding to directories that (a) contain .java or
   * .class files and (b) are not in either of the "skip lists" specified
   * on the command line. Use the {@link #processArgs} method to specify
   * a list of directories to process as well as lists of packages to
   * skip, or else manually set the {@link #namesToSkip} and {@link #pkgsToSkip}
   * lists before invoking this method.
   *
   * @param root  The root of the package tree.
   * @param dir   The directory to search.
   */
  public static LinkedList findPkgs(File root, File dir) {
    // See if we should skip this directory
    ListIterator skipNames = namesToSkip.listIterator();
    while (skipNames.hasNext()) {
      Specifier skipName = (Specifier) skipNames.next();
      if (skipName.match(dir.getName())) {
         return pkgList;
      }
    }
    ListIterator skipPkgs = pkgsToSkip.listIterator();
    while (skipPkgs.hasNext()) {
      PathSpecifier skipPkg = (PathSpecifier) skipPkgs.next();
      if (skipPkg.match(pkgName(root, dir), ".")) {
         return pkgList;
      }
    }

    // Look for a source file or class file in this dir
    String files[] = dir.list();
    for (int i=0; i<files.length; i++) {
      if (files[i].endsWith(".class")
      ||  files[i].endsWith(".java") ) {
         if (root.equals(dir)) {
            pkgList.add("(anonymous)");
         } else {
            // Get path to this dir, chop off the part that goes
            // to the root directory, and convert the name to a
            // a package by replacing file separator characters
            // with "."
            pkgList.add(pkgName(root, dir));
         }
         break;
      }
    }//for

    // Recurse on subdirectories
    for (int i=0; i<files.length; i++) {
      File file = new File(dir, files[i]);
      if (file.isDirectory()) {
         findPkgs(root,file);
      }
    }//for

    return pkgList;
  }//writeDirs

  /**
   * Return the package name corresponding to the specified directory.
   * Subtracts the "root" path from the directory path and converts
   * the directory-name separators to dots. For directory /x/y/z/foo
   * with root /x/y, the result is z.foo
   *
   * @param root  The root of the package tree.
   * @param dir   The directory to convert to a package name.
   */
  public static String pkgName(File root, File dir) {
    if (root.equals(dir)) return ".";
    char separator = File.separatorChar;
    int rootPathLength = root.getPath().length();
    String fullPath = dir.getPath();
    String localPath = fullPath.substring(rootPathLength+1);
    return localPath.replace(separator,'.');
  }

  /**
   * Delivers usage instructions when the program is invoked with no
   * arguments.
   */
  static protected void echoUsage() {
    System.out.println("usage: java PackageList {options} dir1 dir2 ... > package.lst");
    System.out.println("");
    System.out.println("options:");
    System.out.println("  -skipAll name1:name2:...");
    System.out.println("  -skip    pkg1:pkg2:...");
    System.out.println("where:");
    System.out.println("  * -skipAll designates a colon-separated list of directory names to be");
    System.out.println("     skipped at any level of the hierarchy where they occur");
    System.out.println("  * -skip designates a colon-separated list of fully-qualified package names");
    System.out.println("     to be skipped");
    System.out.println("  * the names and packages listed can be Specifier patterns");
    System.out.println("    or PathSpecifer patterns, respectively");
    System.out.println("  * the remaining command line arguments are directory paths");
    System.out.println("    to the packages");
    System.out.println("Example:");
    System.out.println("  java PackageList -skipAll SCCS:RCS:CVS -skip x.y.z  myDir");
  }//echoUsage

  /**
   * Process the command line to
   */
  static protected void processArgs(String[] args) {
     for (int i=0; i<args.length; i++) {
        if (args[i].equals("-skipAll")) {
           i++;
           if (i >= args.length) {
              System.out.println("No list of names after -skipAll option");
              System.exit(1);
           }
           String names = args[i];
           StringTokenizer st = new StringTokenizer(names, ":");
           while (st.hasMoreTokens()) {
              String name = st.nextToken();
              namesToSkip.add(new Specifier(name));
           }
           continue;
        } else if (args[i].equals("-skip")) {
           i++;
           if (i >= args.length) {
              System.out.println("No package list after -skip option");
              System.exit(1);
           }
           String pkgs = args[i];
           StringTokenizer st = new StringTokenizer(pkgs, ":");
           while (st.hasMoreTokens()) {
              String pkg = st.nextToken();
              pkgsToSkip.add(new PathSpecifier(pkg, "."));
           }
           continue;
        } else {
           // Directory name
           rootPaths.add(args[i]);
        }
     }//for

     if (rootPaths.size() == 0) {
        System.out.println("No root directories specified on command line.");
        System.exit(1);
     }
  }//processArgs

}
