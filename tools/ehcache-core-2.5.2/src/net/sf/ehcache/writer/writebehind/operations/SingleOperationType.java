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
package net.sf.ehcache.writer.writebehind.operations;

/**
 * Enum class to provide distinct operation types for each single operation.
 * <p/>
 * The order of the entries in the enum is important since it is used to determine the order of execution of
 * batched operations.
 *
 * @author Geert Bevin
 * @version $Id: SingleOperationType.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public enum SingleOperationType {
    /**
     * For WriteOperation
     */
    WRITE,

    /**
     * For DeleteOperation
     */
    DELETE
}
