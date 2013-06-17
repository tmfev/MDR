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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.util.PropertyUtil;

import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Builds a listener based on RMI.
 * <p/>
 * Expected configuration line:
 * <p/>
 * <code>
 * &lt;cachePeerListenerFactory class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
 * properties="hostName=localhost, port=5000" /&gt;
 * </code>
 *
 * @author Greg Luck
 * @version $Id: RMICacheManagerPeerListenerFactory.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public class RMICacheManagerPeerListenerFactory extends CacheManagerPeerListenerFactory {

    /**
     * The default timeout for cache replication for a single replication action.
     * This may need to be increased for large data transfers.
     */
    public static final Integer DEFAULT_SOCKET_TIMEOUT_MILLIS = Integer.valueOf(120000);

    private static final String HOSTNAME = "hostName";
    private static final String PORT = "port";
    private static final String REMOTE_OBJECT_PORT = "remoteObjectPort";
    private static final String SOCKET_TIMEOUT_MILLIS = "socketTimeoutMillis";

    /**
     * @param properties implementation specific properties. These are configured as comma
     *                   separated name value pairs in ehcache.xml
     */
    public final CacheManagerPeerListener createCachePeerListener(CacheManager cacheManager, Properties properties)
            throws CacheException {
        String hostName = PropertyUtil.extractAndLogProperty(HOSTNAME, properties);

        String portString = PropertyUtil.extractAndLogProperty(PORT, properties);
        Integer port = null;
        if (portString != null && portString.length() != 0) {
            port = Integer.valueOf(portString);
        } else {
            port = Integer.valueOf(0);
        }

        //0 means any port in UnicastRemoteObject, so it is ok if not specified to make it 0
        String remoteObjectPortString = PropertyUtil.extractAndLogProperty(REMOTE_OBJECT_PORT, properties);
        Integer remoteObjectPort = null;
        if (remoteObjectPortString != null && remoteObjectPortString.length() != 0) {
            remoteObjectPort = Integer.valueOf(remoteObjectPortString);
        } else {
            remoteObjectPort = Integer.valueOf(0);
        }

        String socketTimeoutMillisString = PropertyUtil.extractAndLogProperty(SOCKET_TIMEOUT_MILLIS, properties);
        Integer socketTimeoutMillis;
        if (socketTimeoutMillisString == null || socketTimeoutMillisString.length() == 0) {
            socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT_MILLIS;
        } else {
            socketTimeoutMillis = Integer.valueOf(socketTimeoutMillisString);
        }
        return doCreateCachePeerListener(hostName, port, remoteObjectPort, cacheManager, socketTimeoutMillis);
    }

    /**
     * A template method to actually create the factory
     *
     * @param hostName
     * @param port
     * @param remoteObjectPort
     * @param cacheManager
     * @param socketTimeoutMillis @return a crate CacheManagerPeerListener
     */
    protected CacheManagerPeerListener doCreateCachePeerListener(String hostName,
                                                                 Integer port,
                                                                 Integer remoteObjectPort,
                                                                 CacheManager cacheManager,
                                                                 Integer socketTimeoutMillis) {
        try {
            return new RMICacheManagerPeerListener(hostName, port, remoteObjectPort, cacheManager, socketTimeoutMillis);
        } catch (UnknownHostException e) {
            throw new CacheException("Unable to create CacheManagerPeerListener. Initial cause was " + e.getMessage(), e);
        }
    }
}
