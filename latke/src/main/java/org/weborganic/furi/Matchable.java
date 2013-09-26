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


import java.util.regex.Pattern;


/**
 * A class implementing this interface can be matched.
 * 
 * This interface can be used to indicate whether a class can be used for pattern matching.
 * 
 * @author Christophe Lauret
 * @version 4 February 2009
 */
public interface Matchable {

    /**
     * Indicates whether this token matches the specified part of a URL.
     * 
     * @param part The part of URL to test for matching.
     * 
     * @return <code>true</code> if it matches; <code>false</code> otherwise.
     */
    boolean match(String part);

    /**
     * Returns a regular expression pattern corresponding to this object.
     * 
     * @return The regular expression pattern corresponding to this object.
     */
    Pattern pattern();

}
