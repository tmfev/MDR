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

package net.sf.ehcache.management;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;

/**
 * An MBean implementation for those attributes and operations we wish to expose on net.sf.ehcache.CacheManager.
 * This class is not Serializable because it is an adapter around a net.sf.ehcache.CacheManager, which is itself
 * not Serializable. 
 * @author Greg Luck
 * @version $Id: CacheManager.java 3352 2010-12-15 15:57:29Z lorban $
 * @since 1.3
 */
public class CacheManager implements CacheManagerMBean {

    private net.sf.ehcache.CacheManager cacheManager;
    private ObjectName objectName;

    /**
     * Create a management CacheManager
     * @param cacheManager
     * @throws net.sf.ehcache.CacheException
     */
    public CacheManager(net.sf.ehcache.CacheManager cacheManager) throws CacheException {
        this.cacheManager = cacheManager;
        objectName = createObjectName(cacheManager);
    }

    /**
     * Creates an object name using the scheme "net.sf.ehcache:type=CacheManager,name=<cacheManagerName>"
     */
    static ObjectName createObjectName(net.sf.ehcache.CacheManager cacheManager) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheManager,name="
                    + EhcacheHibernateMbeanNames.mbeanSafe(cacheManager.getName()));
        } catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    /**
     * Gets the status attribute of the Ehcache
     *
     * @return The status value, as a String from the Status enum class
     */
    public String getStatus() {
        return cacheManager.getStatus().toString();
    }
    
    /**
     * Gets the name of the CacheManager
     * 
     * @return The cache manager name, which may not be set
     */
    public String getName() {
        return cacheManager.getName();
    }

    /**
     * Shuts down the CacheManager.
    * <p/>
    * If the shutdown occurs on the singleton, then the singleton is removed, so that if a singleton access method
    * is called, a new singleton will be created.
    */
   public void shutdown() {
        cacheManager.shutdown();
    }

    /**
     * Clears  the contents of all caches in the CacheManager, but without
     * removing any caches.
     * <p/>
     * This method is not synchronized. It only guarantees to clear those elements in a cache
     * at the time that the {@link net.sf.ehcache.Ehcache#removeAll()} mehod  on each cache is called.
     */
    public void clearAll() {
        cacheManager.clearAll();
    }

    /**
     * Returns a JMX Cache bean
     */
    public Cache getCache(String name) {
        return new Cache(cacheManager.getEhcache(name));
    }

    /**
     * Gets the cache names managed by the CacheManager
     */
    public String[] getCacheNames() throws IllegalStateException {
        return cacheManager.getCacheNames();
    }

    /**
     * Gets a list of caches in this CacheManager
     *
     * @return a list of JMX Cache objects
     */
    public List getCaches() {
        List cacheList = new ArrayList();
        String[] caches = getCacheNames();
        for (String cacheName : caches) {
            Cache cache = getCache(cacheName);
            cacheList.add(cache);
        }
        return cacheList;
    }

    /**
     * {@inheritDoc}
     */
    public long getTransactionCommittedCount() {
        return cacheManager.getTransactionController().getTransactionCommittedCount();
    }

    /**
     * {@inheritDoc}
     */
    public long getTransactionRolledBackCount() {
        return cacheManager.getTransactionController().getTransactionRolledBackCount();
    }

    /**
     * {@inheritDoc}
     */
    public long getTransactionTimedOutCount() {
        return cacheManager.getTransactionController().getTransactionTimedOutCount();
    }


    /**
     * @return the object name for this MBean
     */
    ObjectName getObjectName() {
        return objectName;
    }
}
