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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * A class to hold a collection of parameters for use during the expansion process.
 * 
 * It provides more convenient functions than the underlying map and handles the rules for parameter
 * values.
 * 
 * @author Christophe Lauret
 * @version 5 November 2009
 */
public class URIParameters implements Parameters {

    /**
     * Maps the parameter names to the values.
     */
    private Map<String, String[]> _parameters;

    /**
     * Creates a new instance.
     */
    public URIParameters() {
        this._parameters = new HashMap<String, String[]>();
    }

    /**
     * Creates a new instance from the specified map.
     * 
     * @param parameters The map of parameters to supply
     */
    public URIParameters(Map<String, String[]> parameters) {
        this._parameters = new HashMap<String, String[]>(parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String value) {
        if (value == null) {
            return;
        }
        this._parameters.put(name, new String[] {value});
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values) {
        if (values == null) {
            return;
        }
        this._parameters.put(name, values);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> names() {
        return Collections.unmodifiableSet(this._parameters.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(String name) {
        String[] vals = this._parameters.get(name);

        if (vals == null || vals.length == 0) {
            return null;
        } else {
            return vals[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues(String name) {
        return this._parameters.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String name) {
        return this._parameters.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasValue(String name) {
        String[] values = this._parameters.get(name);

        return values != null && values.length > 0 && values[0].length() > 0;
    }

}
