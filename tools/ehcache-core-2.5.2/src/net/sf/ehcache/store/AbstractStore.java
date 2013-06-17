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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;

/**
 * @author gkeim
 *
 */
public abstract class AbstractStore implements Store {

    /**
     * listener list
     */
    private transient List<StoreListener> listenerList;

    /**
     * onLoad initializer
     */
    protected synchronized List<StoreListener> getEventListenerList() {
        if (listenerList == null) {
            listenerList = new ArrayList<StoreListener>();
        }
        return listenerList;
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#isCacheCoherent()
     */
    public boolean isCacheCoherent() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#isClusterCoherent()
     */
    public boolean isClusterCoherent() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#isNodeCoherent()
     */
    public boolean isNodeCoherent() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#setNodeCoherent(boolean)
     */
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws InterruptedException
     * @throws TerracottaNotRunningException
     *
     * @see net.sf.ehcache.store.Store#waitUntilClusterCoherent()
     */
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#addStoreListener(net.sf.ehcache.store.StoreListener)
     */
    public synchronized void addStoreListener(StoreListener listener) {
        removeStoreListener(listener);
        getEventListenerList().add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see net.sf.ehcache.store.Store#removeStoreListener(net.sf.ehcache.store.StoreListener)
     */
    public synchronized void removeStoreListener(StoreListener listener) {
        getEventListenerList().remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        if (!extractors.isEmpty()) {
            throw new InvalidConfigurationException("Search attributes not supported by this store type: " + getClass().getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Results executeQuery(StoreQuery query) {
        throw new UnsupportedOperationException("Query execution not supported by this store type: " + getClass().getName());
    }


    /**
     * {@inheritDoc}
     */
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Collection<Element> elements) throws CacheException {
        for (Element element : elements) {
            put(element);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeAll(Collection<?> keys) {
        for (Object key : keys) {
            remove(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        Map<Object, Element> elements = new HashMap<Object, Element>();
        for (Object key : keys) {
            elements.put(key, getQuiet(key));
        }
        return elements;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Object, Element> getAll(Collection<?> keys) {
        Map<Object, Element> elements = new HashMap<Object, Element>();
        for (Object key : keys) {
            elements.put(key, get(key));
        }
        return elements;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasAbortedSizeOf() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void recalculateSize(Object key) {
        // overriden on necessity
    }
}
