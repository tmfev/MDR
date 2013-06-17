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

package net.sf.ehcache.store;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.RemoteCacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author Alex Snaps
 */
public class DiskStoreBootstrapCacheLoader extends MemoryLimitedCacheLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DiskStoreBootstrapCacheLoader.class);

    private final boolean asynchronous;
    private final long delay;
    private volatile boolean doneLoading;
    private volatile int loadedElements;

    /**
     * Constructor for testing purposes
     * Will delay before starting the asynchronous load process
     * @param delay in milliseconds
     */
    DiskStoreBootstrapCacheLoader(final long delay) {
        asynchronous = true;
        this.delay = delay;
    }

    /**
     * Constructor for loader
     * @param asynchronous whether load is asynchronous or synchronous
     */
    public DiskStoreBootstrapCacheLoader(final boolean asynchronous) {
        this.asynchronous = asynchronous;
        delay = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void load(final Ehcache cache) throws CacheException {
        if (cache.getCacheConfiguration().isDiskPersistent()) {
            if (asynchronous) {
                BootstrapThread thread = new BootstrapThread(cache);
                thread.start();
            } else {
                doLoad(cache);
            }
        } else {
            LOG.warn("Cache '" + cache.getName() + "' isn't disk persistent, nothing to laod from!");
        }
    }

    private void doLoad(Ehcache cache) {
        loadedElements = 0;
        try {
            final Iterator iterator = cache.getKeys().iterator();
            while (iterator.hasNext() && !isInMemoryLimitReached(cache, loadedElements)) {
                if (cache.get(iterator.next()) != null) {
                    ++loadedElements;
                }
            }
        } finally {
            doneLoading = true;
        }
        LOG.debug("Loaded {} elements from disk into heap for cache {}", loadedElements, cache.getName());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Checks whether we're done loading yet
     * @return true if done, false if still loading
     */
    boolean isDoneLoading() {
        return doneLoading;
    }

    /**
     * Amount of elements loaded by the instance
     * @return elements loaded
     */
    int getLoadedElements() {
        return loadedElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * A background daemon thread that asynchronously calls doLoad
     */
    private final class BootstrapThread extends Thread {
        private Ehcache cache;

        public BootstrapThread(Ehcache cache) {
            super("Bootstrap Thread for cache " + cache.getName());
            this.cache = cache;
            setDaemon(true);
            setPriority(Thread.NORM_PRIORITY);
        }

        /**
         * RemoteDebugger thread method.
         */
        public final void run() {
            try {
                sleep(delay);
                try {
                    doLoad(cache);
                } catch (RemoteCacheException e) {
                    LOG.warn("Error asynchronously performing bootstrap. The cause was: " + e.getMessage(), e);
                }
            } catch (InterruptedException e) {
                interrupted();
            } finally {
                cache = null;
            }
        }
    }
}
