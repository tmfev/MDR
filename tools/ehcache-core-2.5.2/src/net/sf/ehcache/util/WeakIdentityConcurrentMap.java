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

package net.sf.ehcache.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A poor man's implementation of a WeakIdentityConcurrentMap to hold the CacheManager associated ExecutorServices
 * @param <K> The key type
 * @param <V> The value type
 * @author Alex Snaps
 */
public final class WeakIdentityConcurrentMap<K, V> {

    private ConcurrentMap<WeakReference<K>, V> map = new ConcurrentHashMap<WeakReference<K>, V>();
    private ReferenceQueue<K> queue = new ReferenceQueue<K>();

    private final CleanUpTask<V> cleanUpTask;

    /**
     * Constructor
     */
    public WeakIdentityConcurrentMap() {
      this(null);
    }
    
    /**
     * Constructor
     * @param cleanUpTask
     */
    public WeakIdentityConcurrentMap(final CleanUpTask<V> cleanUpTask) {
        this.cleanUpTask = cleanUpTask;
    }

    /**
     * Puts into the underlying
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {
        cleanUp();
        return map.put(new IdentityWeakReference<K>(key, queue), value);
    }
    
    /**
     * Puts into the underlying
     * @param key
     * @param value
     * @return
     */
    public V putIfAbsent(K key, V value) {
        cleanUp();
        return map.putIfAbsent(new IdentityWeakReference<K>(key, queue), value);
    }

    /**
     * @param key
     * @return
     */
    public V get(K key) {
        cleanUp();
        return map.get(new IdentityWeakReference<K>(key));
    }

    /**
     *
     */
    public void cleanUp() {

        Reference<? extends K> reference;
        while ((reference = queue.poll()) != null) {
            final V value = map.remove(reference);
            if (cleanUpTask != null && value != null) {
                cleanUpTask.cleanUp(value);
            }
        }
    }

    /**
     *
     * @return
     */
    public Set<K> keySet() {
        cleanUp();
        K k;
        final HashSet<K> ks = new HashSet<K>();
        for (WeakReference<K> weakReference : map.keySet()) {
            k = weakReference.get();
            if (k != null) {
                ks.add(k);
            }
        }
        return ks;
    }

    /**
     * @param <T>
     */
    private static final class IdentityWeakReference<T> extends WeakReference<T> {

        private final int hashCode;

        /**
         * @param reference
         */
        IdentityWeakReference(T reference) {
            this(reference, null);
        }

        /**
         * @param reference
         * @param referenceQueue
         */
        IdentityWeakReference(T reference, ReferenceQueue<T> referenceQueue) {
            super(reference, referenceQueue);
            this.hashCode = (reference == null) ? 0 : System.identityHashCode(reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IdentityWeakReference<?>)) {
                return false;
            } else {
                IdentityWeakReference<?> wr = (IdentityWeakReference<?>)o;
                Object got = get();
                return (got != null && got == wr.get());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    /**
     * @param <T>
     */
    public static interface CleanUpTask<T> {

        /**
         * @param object
         */
        void cleanUp(T object);
    }
}
