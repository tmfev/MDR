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
package net.sf.ehcache.writer.writebehind;

import java.util.List;

/**
 * An interface for implementing a filter for operations before they are processed. By filtering the outstanding
 * operations it's for example possible to remove scheduled work before it's actually executed.
 *
 * @param <T> the operation type that is used by the filter
 * @author Geert Bevin
 * @version $Id: OperationsFilter.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public interface OperationsFilter<T> {
    /**
     * Filter the operations of a write behind queue.
     *
     * @param operations the operations to filter
     * @param converter  the converter to use for each operation
     */
    public void filter(List operations, OperationConverter<T> converter);
}
