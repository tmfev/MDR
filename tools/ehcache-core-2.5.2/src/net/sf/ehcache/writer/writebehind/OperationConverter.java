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

/**
 * Interface that allows an converter to be implemented that can create an operation based
 * on an arbitrary object.
 *
 * @param <T> the operation type that should be converted to
 * @author Geert Bevin
 * @version $Id: OperationConverter.java 3883 2011-04-09 18:17:18Z hhuynh $
 */
public interface OperationConverter<T> {
    /**
     * Convert an arbitrary object
     *
     * @param source the object to convert
     * @return the converted operation instance
     */
    public T convert(Object source);
}
