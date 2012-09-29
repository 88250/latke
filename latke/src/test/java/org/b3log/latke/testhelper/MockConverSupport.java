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
package org.b3log.latke.testhelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.b3log.latke.servlet.converter.ConvertSupport;

/**
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 3, 2012
 */
public class MockConverSupport extends ConvertSupport {

    @Override
    public Object convert(final String pName, final Object value, final Class<?> clazz) {

        if (clazz == java.util.Date.class) {
            final String data = (String) value;
            final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

            try {
                return format.parse(data);
            } catch (final ParseException e) {
                e.printStackTrace();
            }

        }

        return super.convert(pName, value, clazz);
    }

}
