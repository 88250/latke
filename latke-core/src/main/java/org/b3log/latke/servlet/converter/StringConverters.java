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


import java.util.HashMap;
import java.util.Map;


/**
 * the util for StringConverter ,//TODO should be design for user custom!!!
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 10, 2012
 */
public final class StringConverters {

    /**
     * the default constructor.
     */
    private StringConverters() {}

    /**
     * the map hold all the converter!.bad desgin,shuold for user custom.
     */
    private static Map<Class<?>, IStringConvert<?>> converMap = new HashMap<Class<?>, IStringConvert<?>>();

    static {
        registerConverter(String.class, new StringToStringConvert());
        registerConverter(Integer.class, new StringToIntergerConvert());
    }

    /**
     * registerConverter.
     * @param clazz the specific class
     * @param convert the {@link IStringConvert}
     */
    public static void registerConverter(final Class<?> clazz,
        final IStringConvert<?> convert) {
        converMap.put(clazz, convert);
    }

    /**
     * core method from String to the class Type.
     * @param name the name of the param
     * @param value the String value
     * @param clazz the specific clazz
     * @param <T> the type to be convert 
     * @return the specific type
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(final String name, final String value, final Class<T> clazz) {

        final IStringConvert<?> convert = converMap.get(clazz);

        if (convert != null) {
            return (T) converMap.get(clazz).doConvert(value);
        }
        return null;
    }
}
