/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;


import java.util.Hashtable;
import java.util.Map;


/**
 * A variable resolver backed by a values mapped to objects.
 * 
 * @author Christophe Lauret
 * @version 30 December 2008
 */
public class VariableResolverMap<V> implements VariableResolver {

    /**
     * The list of values.
     */
    private Map<String, ? extends V> _map;

    /**
     * Creates a new variable resolver.
     */
    public VariableResolverMap() {
        this._map = new Hashtable<String, V>();
    }

    /**
     * Creates a new variable resolver from the given map.
     * 
     * @param map Variable values mapped to objects.
     */
    public VariableResolverMap(Map<String, ? extends V> map) {
        this._map = map;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String value) {
        if (value == null) {
            return false;
        }
        return this._map.containsKey(value);
    }

    /**
     * {@inheritDoc}
     */
    public V resolve(String value) {
        return this._map.get(value);
    }

}
