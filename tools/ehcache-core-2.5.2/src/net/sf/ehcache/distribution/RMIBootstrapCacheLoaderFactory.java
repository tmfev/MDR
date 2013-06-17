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

package net.sf.ehcache.distribution;

import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * A factory to create a configured RMIBootstrapCacheLoader
 * @author Greg Luck
 * @version $Id: RMIBootstrapCacheLoaderFactory.java 4280 2011-07-04 13:31:39Z alexsnaps $
 */
public class RMIBootstrapCacheLoaderFactory extends BootstrapCacheLoaderFactory<RMIBootstrapCacheLoader> {


    /**
     * The property name expected in ehcache.xml for the maximum chunk size in bytes
     */
    public static final String MAXIMUM_CHUNK_SIZE_BYTES = "maximumChunkSizeBytes";

    /**
     * The default maximum serialized size of the elements to request from a remote cache peer during bootstrap.
     */
    protected static final int DEFAULT_MAXIMUM_CHUNK_SIZE_BYTES = 5000000;

    /**
     * The highest reasonable chunk size in bytes
     */
    protected static final int ONE_HUNDRED_MB = 100000000;

    /**
     * The lowest reasonable chunk size in bytes
     */
    protected static final int FIVE_KB = 5000;

    private static final Logger LOG = LoggerFactory.getLogger(RMIBootstrapCacheLoaderFactory.class.getName());


    /**
     * Create a <code>BootstrapCacheLoader</code>
     *
     * @param properties implementation specific properties. These are configured as comma
     *                   separated name value pairs in ehcache.xml
     * @return a constructed BootstrapCacheLoader
     */
    public RMIBootstrapCacheLoader createBootstrapCacheLoader(Properties properties) {
        boolean bootstrapAsynchronously = extractBootstrapAsynchronously(properties);
        int maximumChunkSizeBytes = extractMaximumChunkSizeBytes(properties);
        return new RMIBootstrapCacheLoader(bootstrapAsynchronously, maximumChunkSizeBytes);
    }

    /**
     *
     * @param properties the properties passed by the CacheManager, read from the configuration file
     * @return the max chunk size in bytes
     */
    protected int extractMaximumChunkSizeBytes(Properties properties) {
        int maximumChunkSizeBytes;
        String maximumChunkSizeBytesString = PropertyUtil.extractAndLogProperty(MAXIMUM_CHUNK_SIZE_BYTES, properties);
        if (maximumChunkSizeBytesString != null) {
            try {
                int maximumChunkSizeBytesCandidate = Integer.parseInt(maximumChunkSizeBytesString);
                if ((maximumChunkSizeBytesCandidate < FIVE_KB) || (maximumChunkSizeBytesCandidate > ONE_HUNDRED_MB)) {
                    LOG.warn("Trying to set the chunk size to an unreasonable number. Using the default instead.");
                    maximumChunkSizeBytes = DEFAULT_MAXIMUM_CHUNK_SIZE_BYTES;
                } else {
                    maximumChunkSizeBytes = maximumChunkSizeBytesCandidate;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Number format exception trying to set chunk size. Using the default instead.");
                maximumChunkSizeBytes = DEFAULT_MAXIMUM_CHUNK_SIZE_BYTES;
            }

        } else {
            maximumChunkSizeBytes = DEFAULT_MAXIMUM_CHUNK_SIZE_BYTES;
        }
        return maximumChunkSizeBytes;
    }


}
