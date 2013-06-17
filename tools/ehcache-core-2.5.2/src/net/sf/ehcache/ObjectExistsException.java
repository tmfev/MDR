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


package net.sf.ehcache;

/**
 * Thrown when a duplicate cache is attemtpted to be created
 *
 * @author Greg Luck, Claus Ibsen
 * @version $Id: ObjectExistsException.java 2154 2010-04-06 02:45:52Z cdennis $
 */
public final class ObjectExistsException extends CacheException {

    /**
     * Constructor for the ObjectExistsException object.
     */
    public ObjectExistsException() {
        super();
    }


    /**
     * Constructor for the ObjectExistsException object.
     *
     * @param message
     */
    public ObjectExistsException(String message) {
        super(message);
    }

}
