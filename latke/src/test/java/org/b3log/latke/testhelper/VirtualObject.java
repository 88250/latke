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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * a virtual Object just to aceess private esaily,just fo UT.
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 3, 2012
 */
public class VirtualObject {

    /**
     * the real instance holder.
     */
    private Object realObject = null;

    /**
     * the clazz of the real instance.
     */
    private Class<?> clazz = null;

    /**
     * the public contructor.
     * @param className the className of a Object
     */
    public VirtualObject(final String className) {

        try {
            clazz = Class.forName(className);
            final Constructor<?> constructor = clazz
                    .getDeclaredConstructor(new Class<?>[] {});
            constructor.setAccessible(true);
            realObject = constructor.newInstance(new Object[] {});

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * set the value of a field.
     * @param fieldName the fieldName of a Object
     * @param value the value to the filed
     */
    public void setValue(final String fieldName, final Object value) {
        try {
            // now only DeclaredField.
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(realObject, value);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get a FieldValue.
     * @param fieldName the fieldName
     * @return the value of a Object.
     */
    public Object getValue(final String fieldName) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(realObject);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * get the realObject holder.
     * @return  the real object
     */
    public Object getInstance() {
        return realObject;
    }

    /**
     * get the class of the Instance.
     * @return the class
     */
    public Class<?> getInstanceClass() {
        return clazz;
    }

    /**
     * return one method of the Instance.
     * @param name the methodName
     * @param parameterTypes the param Types
     * @return the method
     */
    public Method getInstanceMethod(final String name,
            final Class<?>... parameterTypes) {

        try {
            final Method method = clazz.getMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
