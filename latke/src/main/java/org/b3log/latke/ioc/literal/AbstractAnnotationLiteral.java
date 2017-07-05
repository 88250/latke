/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.ioc.literal;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * Supports inline instantiation of annotation type instances.
 *
 * @param <T> the annotation type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 27, 2009
 */
public abstract class AbstractAnnotationLiteral<T extends Annotation> implements Annotation {

    /**
     * Annotation type.
     */
    private Class<T> annotationType;

    /**
     * Members.
     */
    private Method[] members;

    /**
     * Hash base.
     */
    private static final int HASH_BASE = 127;

    /**
     * Construct an annotation literal.
     */
    protected AbstractAnnotationLiteral() {
        final Class<?> annotationLiteralSubclass = getAnnotationLiteralSubclass(getClass());

        if (annotationLiteralSubclass == null) {
            throw new RuntimeException(getClass() + "is not a subclass of AnnotationLiteral ");
        }

        annotationType = getTypeParameter(annotationLiteralSubclass);

        if (annotationType == null) {
            throw new RuntimeException(getClass() + " is missing type parameter in AnnotationLiteral");
        }

        this.members = annotationType.getDeclaredMethods();
    }

    /**
     * Gets the annotation literal subclass of the specified class.
     * 
     * @param clazz the specified class
     * @return annotation literal subclass
     */
    private static Class<?> getAnnotationLiteralSubclass(final Class<?> clazz) {
        final Class<?> superclass = clazz.getSuperclass();

        if (superclass.equals(AbstractAnnotationLiteral.class)) {
            return clazz;
        } else if (superclass.equals(Object.class)) {
            return null;
        } else {
            return getAnnotationLiteralSubclass(superclass);
        }
    }

    /**
     * Gets type parameter of the specified annotation literal superclass.
     * 
     * @param <T> the type of returned class
     * @param annotationLiteralSuperclass the specified annotation literal superclass
     * @return type parameter
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getTypeParameter(final Class<?> annotationLiteralSuperclass) {
        final Type type = annotationLiteralSuperclass.getGenericSuperclass();

        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;

            if (parameterizedType.getActualTypeArguments().length == 1) {
                return (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }
        }

        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    @Override
    public String toString() {
        String string = "@" + annotationType().getName() + "(";

        for (int i = 0; i < members.length; i++) {
            string += members[i].getName() + "=";
            string += invoke(members[i], this);
            if (i < members.length - 1) {
                string += ",";
            }
        }
        return string + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Annotation) {
            final Annotation that = (Annotation) other;

            if (this.annotationType().equals(that.annotationType())) {
                for (Method member : members) {
                    final Object thisValue = invoke(member, this);
                    final Object thatValue = invoke(member, that);

                    if (thisValue.getClass().isArray() && thatValue.getClass().isArray()) {
                        if (!Arrays.equals(Object[].class.cast(thisValue), Object[].class.cast(thatValue))) {
                            return false;
                        }
                    } else if (!thisValue.equals(thatValue)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        for (Method member : members) {
            final int memberNameHashCode = HASH_BASE * member.getName().hashCode();
            final Object value = invoke(member, this);
            final int memberValueHashCode = value.getClass().isArray() ? Arrays.hashCode(Object[].class.cast(value)) : value.hashCode();

            hashCode += memberNameHashCode ^ memberValueHashCode;
        }
        return hashCode;
    }

    /**
     * Invokes the specified method on the specified instance.
     * 
     * @param method the specified method
     * @param instance the specified instance
     * @return invoke returned value
     */
    private static Object invoke(final Method method, final Object instance) {
        try {
            method.setAccessible(true);

            return method.invoke(instance);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
        }
    }
}
