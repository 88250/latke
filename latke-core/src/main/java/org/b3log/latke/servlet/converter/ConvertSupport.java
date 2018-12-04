/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.servlet.converter;


/**
 * the interface for user to custom the convert.
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 12, 2012
 */
public class ConvertSupport {

    /**
     * convert a class value to another value.
     * @param pName the paramName
     * @param value the value of the paramName
     * @param clazz the clazz to be converted to
     * @return the converted Object
     */
    public Object convert(final String pName, final Object value, final Class<?> clazz) {
        
        if (value instanceof String) {
            return StringConverters.convert(pName, (String) value, clazz);
        }
        // TODO
        return null;
    }

}
