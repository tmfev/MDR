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

package net.sf.ehcache.bootstrap;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;

/**
 * @author Greg Luck
 * @version $Id: BootstrapCacheLoader.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public interface BootstrapCacheLoader {

    /**
     * Instructs the loader to load the given cache
     * @param cache
     */
    void load(Ehcache cache) throws CacheException;

    /**
     *
     * @return true if this bootstrap loader is asynchronous
     */
    boolean isAsynchronous();

    /**
     * Clones the loader
     */
    Object clone() throws CloneNotSupportedException;

}
