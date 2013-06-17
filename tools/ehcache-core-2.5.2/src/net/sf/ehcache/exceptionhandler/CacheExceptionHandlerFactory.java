/**
 *  Copyright 2003-2010 Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.exceptionhandler;

import java.util.Properties;

/**
 * An abstract factory for creating <code>CacheExceptionHandler</code>s at configuration time, in ehcache.xml.
 * <p/>
 * Extend to create a concrete factory
 *
 * @author <a href="mailto:gluck@gregluck.com">Greg Luck</a>
 * @version $Id: CacheExceptionHandlerFactory.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public abstract class CacheExceptionHandlerFactory {

    /**
     * Create an <code>CacheExceptionHandler</code>
     *
     * @param properties implementation specific properties. These are configured as comma
     *                   separated name value pairs in ehcache.xml
     * @return a constructed CacheExceptionHandler
     */
    public abstract CacheExceptionHandler createExceptionHandler(Properties properties);


}
