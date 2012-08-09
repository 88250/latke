/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.urlfetch;

/**
 * This class depicts either an HTTP request header or an HTTP response header.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public final class HTTPHeader {

    /**
     * Name.
     */
    private String name;
    /**
     * Value.
     */
    private String value;

    /**
     * Constructs a HTTP header with the specified name and value.
     * 
     * @param name the specified name
     * @param value the specified value
     */
    public HTTPHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name with the specified name.
     * 
     * @param name the specified name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the value.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value with the specified value.
     * 
     * @param value the specified value
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
