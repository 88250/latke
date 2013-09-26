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


import java.util.Map;


/**
 * Defines a token in a URI pattern or template.
 * 
 * <p>All tokens can be represented as a text expression which cannot be
 * <code>null</code>.
 * 
 * <p>Two tokens having the same expression are considered equal.
 * 
 * @author Christophe Lauret
 * @version 30 December 2008
 */
public interface Token extends Expandable {

    /**
     * The expression corresponding to this token.
     * 
     * @return The expression corresponding to this token.
     */
    String expression();

    /**
     * Indicates whether this token can be resolved.
     * 
     * <p>A resolvable token contains variables which can be resolved.
     * 
     * @return <code>true</code> if variables can be resolved from the specified pattern;
     *         <code>false</code> otherwise.
     */
    boolean isResolvable();

    /**
     * Resolves the specified expanded URI part for this token.
     * 
     * <p>The resolution process requires all variables referenced in the token to be mapped to
     * the value that is present in the expanded URI data.
     * 
     * @param expanded The part of the URI that correspond to an expanded version of the token.
     * @param values   The variables mapped to their values as a result of resolution.
     * 
     * @return <code>true</code> this operation was successful; <code>false</code> otherwise.
     */
    public boolean resolve(String expanded, Map<Variable, Object> values);

}
