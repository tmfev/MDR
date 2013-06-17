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

package net.sf.ehcache.pool.sizeof;

import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;

import static net.sf.ehcache.pool.sizeof.JvmInformation.CURRENT_JVM_INFORMATION;

/**
 * SizeOf implementation that relies on a Java agent to be loaded to do the measurement
 * It will try to load the agent through the JDK6 Attach API if available
 * All it's constructor do throw UnsupportedOperationException if the agent isn't present or couldn't be loaded dynamically
 * @author Chris Dennis
 * @author Alex Snaps
 */
public class AgentSizeOf extends SizeOf {

    /**
     * System property name to bypass attaching to the VM and loading of Java agent to measure Object sizes.
     */
    public  static final String  BYPASS_LOADING = "net.sf.ehcache.pool.sizeof.AgentSizeOf.bypass";

    private static final boolean AGENT_LOADED   = !Boolean.getBoolean(BYPASS_LOADING) && AgentLoader.loadAgent();

    /**
     * Builds a new SizeOf that will not filter fields and will cache reflected fields
     * @throws UnsupportedOperationException If agent couldn't be loaded or isn't present
     * @see #AgentSizeOf(net.sf.ehcache.pool.sizeof.filter.SizeOfFilter, boolean)
     */
    public AgentSizeOf() throws UnsupportedOperationException {
        this(new PassThroughFilter());
    }

    /**
     * Builds a new SizeOf that will filter fields according to the provided filter and will cache reflected fields
     * @param filter The filter to apply
     * @throws UnsupportedOperationException If agent couldn't be loaded or isn't present
     * @see #AgentSizeOf(net.sf.ehcache.pool.sizeof.filter.SizeOfFilter, boolean)
     * @see SizeOfFilter
     */
    public AgentSizeOf(SizeOfFilter filter) throws UnsupportedOperationException {
        this(filter, true);
    }

    /**
     * Builds a new SizeOf that will filter fields according to the provided filter
     * @param filter The filter to apply
     * @param caching whether to cache reflected fields
     * @throws UnsupportedOperationException If agent couldn't be loaded or isn't present
     * @see SizeOfFilter
     */
    public AgentSizeOf(SizeOfFilter filter, boolean caching) throws UnsupportedOperationException {
        super(filter, caching);
        if (!AGENT_LOADED) {
            throw new UnsupportedOperationException("Agent not available or loadable");
        }
    }

    @Override
    public long sizeOf(Object obj) {
        return Math.max(CURRENT_JVM_INFORMATION.getMinimumObjectSize(),
                AgentLoader.agentSizeOf(obj) + CURRENT_JVM_INFORMATION.getAgentSizeOfAdjustment());
    }
}
