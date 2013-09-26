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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A variable resolver using a list to resolve values.
 * 
 * For example, to constrain a variable value to a specific list:
 * <pre>
 *  // Create a new variable resolver on a list of values
 *  VariableResolver vr = new VariableResolverList(new String[]{"foo", "bar"});
 *
 *  // Bind the variable resolver to variable type 'sample' (eg. {sample:test})
 *  VariableBinder binder = new VariableBinder();
 *  binder.bindType("sample", vr);
 * </pre>
 * 
 * @see VariableBinder
 * 
 * @author Christophe Lauret
 * @version 11 June 2009
 */
public class VariableResolverList implements VariableResolver {

    /**
     * The list of values.
     */
    private List<String> _values;

    /**
     * Creates a new variable resolver.
     */
    public VariableResolverList() {
        this._values = new ArrayList<String>();
    }

    /**
     * Creates a new variable resolver from the list of values.
     * 
     * @param values The list of values.
     */
    public VariableResolverList(List<String> values) {
        this._values = new ArrayList<String>();
        this._values.addAll(values);
    }

    /**
     * Creates a new variable resolver from the list of values.
     * 
     * @param values The list of values.
     */
    public VariableResolverList(String[] values) {
        this._values = Arrays.asList(values);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String value) {
        return this._values.contains(value);
    }

    /**
     * {@inheritDoc}
     */
    public Object resolve(String value) {
        return exists(value) ? value : null;
    }
}
