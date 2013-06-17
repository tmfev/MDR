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

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;

/**
 * Default socket timeouts are unlikely to be suitable for cache replication. Sockets should
 * fail fast.
 * <p/>
 * This class decorates the RMIClientSocketFactory so as to enable customisations to be placed
 * on newly created sockets.
 *
 * @author <a href="mailto:gluck@thoughtworks.com">Greg Luck</a>
 * @version $Id: ConfigurableRMIClientSocketFactory.java 4260 2011-07-01 13:45:26Z cdennis $
 * @see "http://java.sun.com/j2se/1.5.0/docs/guide/rmi/socketfactory/#1"
 */
public final class ConfigurableRMIClientSocketFactory implements Serializable, RMIClientSocketFactory {

    private static final int ONE_SECOND = 1000;

    private static final long serialVersionUID = 4920508630517373246L;

    private final int socketTimeoutMillis;

    /**
     * Construct a new socket factory with the given timeout.
     *
     * @param socketTimeoutMillis
     * @see Socket#setSoTimeout
     */
    public ConfigurableRMIClientSocketFactory(Integer socketTimeoutMillis) {
        if (socketTimeoutMillis == null) {
            this.socketTimeoutMillis = ONE_SECOND;
        } else {
            this.socketTimeoutMillis = socketTimeoutMillis.intValue();
        }
    }

    /**
     * Create a client socket connected to the specified host and port.
     * <p/>
     * If necessary this implementation can be changed to specify the outbound address to use
     * e.g. <code>Socket socket = new Socket(host, port, localInterface , 0);</code>
     *
     * @param host the host name
     * @param port the port number
     * @return a socket connected to the specified host and port.
     * @throws java.io.IOException if an I/O error occurs during socket creation
     * @since 1.2
     */
    public Socket createSocket(String host, int port) throws IOException {
        Socket socket = getConfiguredRMISocketFactory().createSocket(host, port);
        socket.setSoTimeout(socketTimeoutMillis);
        return socket;
    }

    /**
     * Implements the Object hashCode method.
     *
     * @return a hash based on socket options
     */
    @Override
    public int hashCode() {
        return socketTimeoutMillis;
    }

    /**
     * The standard hashCode method which is necessary for SocketFactory classes.
     * Omitting this method causes RMI to quickly error out
     * with "too many open files" errors.
     *
     * @param object the comparison object
     * @return equal if the classes are the same and the socket options are the name.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else {
            return (getClass() == object.getClass() &&
                socketTimeoutMillis == ((ConfigurableRMIClientSocketFactory) object).socketTimeoutMillis);
        }
    }

    /**
     * Return the JVM-level configured {@code RMISocketFactory}.
     * <p>
     * If a global socket factory has been set via the
     * {@link RMISocketFactory#setSocketFactory(RMISocketFactory)} method then
     * that factory will be returned.  Otherwise the default socket factory as
     * returned by {@link RMISocketFactory#getDefaultSocketFactory()} is used
     * instead.
     *
     * @return the configured @{code {@link RMISocketFactory}
     */
    public static RMISocketFactory getConfiguredRMISocketFactory() {
        RMISocketFactory globalSocketFactory = RMISocketFactory.getSocketFactory();
        if (globalSocketFactory == null) {
            return RMISocketFactory.getDefaultSocketFactory();
        } else {
            return globalSocketFactory;
        }
    }
}