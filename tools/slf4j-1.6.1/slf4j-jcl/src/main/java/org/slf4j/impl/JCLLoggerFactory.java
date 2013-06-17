/* 
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.helpers.Util;

/**
 * JCLLoggerFactory is an implementation of {@link ILoggerFactory} returning the
 * appropriately named {@link JCLLoggerAdapter} instance.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class JCLLoggerFactory implements ILoggerFactory {

  private static final String JCL_DELEGATION_LOOP_URL = "http://www.slf4j.org/codes.html#jclDelegationLoop";

  // check for delegation loops
  static {
    try {
      Class.forName("org.apache.commons.logging.impl.SLF4JLogFactory");
      String part1 = "Detected both jcl-over-slf4j.jar AND slf4j-jcl.jar on the class path, preempting StackOverflowError. ";
      String part2 = "See also " + JCL_DELEGATION_LOOP_URL
          + " for more details.";

      Util.report(part1);
      Util.report(part2);
      throw new IllegalStateException(part1 + part2);
    } catch (ClassNotFoundException e) {
      // this is the good case
    }
  }

  // key: name (String), value: a JCLLoggerAdapter;
  Map loggerMap;

  public JCLLoggerFactory() {
    loggerMap = new HashMap();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
   */
  public Logger getLogger(String name) {
    Logger logger = null;
    // protect against concurrent access of loggerMap
    synchronized (this) {
      logger = (Logger) loggerMap.get(name);
      if (logger == null) {
        org.apache.commons.logging.Log jclLogger = LogFactory.getLog(name);
        logger = new JCLLoggerAdapter(jclLogger, name);
        loggerMap.put(name, logger);
      }
    }
    return logger;
  }
}
