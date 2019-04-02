/*
 * Copyright (c) 2009-present, b3log.org
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
package org.b3log.latke.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.b3log.latke.ioc.Stereotype;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Reflection utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.6, Sep 30, 2018
 * @since 2.4.19
 */
final public class Reflections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Reflections.class);

    /**
     * Class pool.
     */
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();

    static {
        CLASS_POOL.insertClassPath(new ClassClassPath(Reflections.class));
    }

    /**
     * Private constructor.
     */
    private Reflections() {
    }

    /**
     * Gets stereo types of the specified class.
     *
     * @param clazz the specified class
     * @return stereo types of the specified class
     */
    public static Set<Class<? extends Annotation>> getStereotypes(final Class<?> clazz) {
        final Set<Class<? extends Annotation>> ret = new HashSet<>();

        final Set<Annotation> annotations = getAnnotations(clazz.getAnnotations(), Stereotype.class);
        if (annotations.isEmpty()) {
            return ret;
        }

        for (final Annotation annotation : annotations) {
            ret.add(annotation.annotationType());
        }

        return ret;
    }

    public static <T> Set<Type> getBeanTypes(final Class<T> beanClass) {
        final Set<Type> ret = new HashSet<>();

        ret.add(beanClass);
        Type genericSuperclass = beanClass;

        while (genericSuperclass != Object.class) {
            Type[] genericInterfaces = null;

            if (genericSuperclass instanceof Class<?>) {
                genericInterfaces = ((Class<?>) genericSuperclass).getGenericInterfaces();
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            } else if (genericSuperclass instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) genericSuperclass).getRawType();

                genericInterfaces = ((Class<?>) rawType).getGenericInterfaces();
                genericSuperclass = ((Class<?>) rawType).getGenericSuperclass();
            }

            if (genericSuperclass != Object.class) {
                ret.add(genericSuperclass);
            }

            if (null != genericInterfaces && 0 != genericInterfaces.length) {
                for (final Type genericInterface : genericInterfaces) {
                    ret.add(genericInterface);
                    ret.addAll(getInterfaces((Class<? super T>) genericInterface));
                }
            }
        }

        return ret;
    }

    private static <T> Set<Type> getInterfaces(final Class<T> interfaceClass) {
        final Set<Type> ret = new HashSet<>();
        final Class<?>[] interfaces = interfaceClass.getInterfaces();
        if (0 == interfaces.length) {
            return ret;
        }

        for (final Class<?> i : interfaces) {
            ret.add(i);
            ret.addAll(getInterfaces(i));
        }

        return ret;
    }

    /**
     * Gets annotations match the needed annotation type from the specified annotation.
     *
     * @param annotations          the specified annotations
     * @param neededAnnotationType the needed annotation type
     * @return annotation set, returns an empty set if not found
     */
    private static Set<Annotation> getAnnotations(final Annotation[] annotations, final Class<? extends Annotation> neededAnnotationType) {
        final Set<Annotation> ret = new HashSet<>();

        for (final Annotation annotation : annotations) {
            annotation.annotationType().getAnnotations();
            final Annotation[] metaAnnotations = annotation.annotationType().getAnnotations();
            for (final Annotation metaAnnotation : metaAnnotations) {
                if (metaAnnotation.annotationType().equals(neededAnnotationType)) {
                    ret.add(annotation);
                }
            }
        }

        return ret;
    }

    /**
     * Get method variable names of the specified class, target method name and parameter types.
     *
     * @param clazz            the specific clazz
     * @param targetMethodName the specified target method name
     * @param types            the specified parameter types
     * @return the String[] of names
     */
    public static String[] getMethodVariableNames(final Class<?> clazz, final String targetMethodName, final Class<?>[] types) {
        CtClass cc;
        CtMethod cm = null;

        try {
            if (null == CLASS_POOL.find(clazz.getName())) {
                CLASS_POOL.insertClassPath(new ClassClassPath(clazz));
            }

            cc = CLASS_POOL.get(clazz.getName());
            final CtClass[] ptypes = new CtClass[types.length];

            for (int i = 0; i < ptypes.length; i++) {
                ptypes[i] = CLASS_POOL.get(types[i].getName());
            }
            cm = cc.getDeclaredMethod(targetMethodName, ptypes);
        } catch (final NotFoundException e) {
            LOGGER.log(Level.ERROR, "Get method variable names failed", e);
        }

        if (null == cm) {
            return new String[types.length];
        }

        final MethodInfo methodInfo = cm.getMethodInfo();
        final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        final LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] variableNames = new String[0];

        try {
            variableNames = new String[cm.getParameterTypes().length];
        } catch (final NotFoundException e) {
            LOGGER.log(Level.ERROR, "Get method variable names failed", e);
        }

        // final int staticIndex = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        int j = -1;
        String variableName = null;
        Boolean ifkill = false;

        while (!"this".equals(variableName)) {
            j++;
            variableName = attr.variableName(j);
            // to prevent heap error when there being some unknown reasons to resolve the VariableNames
            if (j > 99) {
                LOGGER.log(Level.WARN,
                        "Maybe resolve to VariableNames error [class=" + clazz.getName() + ", targetMethodName=" + targetMethodName + ']');
                ifkill = true;
                break;
            }
        }

        if (!ifkill) {
            for (int i = 0; i < variableNames.length; i++) {
                variableNames[i] = attr.variableName(++j);
            }
        }
        return variableNames;
    }

    public static boolean isConcrete(final Type type) {
        return isConcrete((Class<?>) type);
    }

    public static boolean isConcrete(final Class<?> clazz) {
        final int modifiers = clazz.getModifiers();

        return !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers);
    }

    public static boolean isInterface(final Class<?> clazz) {
        return Modifier.isInterface(clazz.getModifiers());
    }

    public static boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isAccessable(final Package subclassPackage, final Package superclassPackage, final int superMemberModifiers) {
        if (subclassPackage.equals(superclassPackage)) {
            switch (superMemberModifiers) {
                case Modifier.PRIVATE:
                    return false;

                case Modifier.PROTECTED:
                    return true;

                case Modifier.PUBLIC:
                    return true;

                default:
                    return true;
            }
        } else {
            switch (superMemberModifiers) {
                case Modifier.PRIVATE:
                    return false;
                case Modifier.PROTECTED:
                    return true;
                case Modifier.PUBLIC:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static Set<Field> getInheritedFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final Set<Field> declaredFieldSet = CollectionUtils.arrayToSet(fields);
        final Set<Field> ret = new HashSet<>(declaredFieldSet);

        Class<?> currentClass = clazz.getSuperclass();
        while (currentClass != null) {
            final Field[] superFields = currentClass.getDeclaredFields();
            for (Field superField : superFields) {
                if (!Modifier.isPrivate(superField.getModifiers()) && !Modifier.isStatic(superField.getModifiers()) && !containField(ret, superField)) {
                    ret.add(superField);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        ret.removeAll(declaredFieldSet);

        return ret;
    }

    public static Set<Field> getHiddenFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final Set<Field> declaredFieldSet = CollectionUtils.arrayToSet(fields);
        final Set<Field> ret = new HashSet<>();

        Class<?> currentClass = clazz.getSuperclass();
        while (currentClass != null) {
            final Field[] superFields = currentClass.getDeclaredFields();
            for (Field superField : superFields) {
                final Field match = getMatch(declaredFieldSet, superField);

                if (!Modifier.isPrivate(superField.getModifiers()) && !Modifier.isStatic(superField.getModifiers()) && match != null) {
                    ret.add(match);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return ret;
    }

    public static Set<Field> getOwnFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final Set<Field> declaredFieldSet = CollectionUtils.arrayToSet(fields);
        final Set<Field> ret = new HashSet<>();
        final Set<Field> inheritedFields = getInheritedFields(clazz);
        final Set<Field> overriddenFields = getHiddenFields(clazz);
        for (final Field declaredField : declaredFieldSet) {
            if (!containField(inheritedFields, declaredField) && !containField(overriddenFields, declaredField)) {
                ret.add(declaredField);
            }
        }

        return ret;
    }

    public static Set<Method> getOwnMethods(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final Set<Method> declaredMethodSet = CollectionUtils.arrayToSet(methods);
        final Set<Method> ret = new HashSet<>();
        final Set<Method> inheritedMethods = getInheritedMethods(clazz);
        final Set<Method> overriddenMethods = getOverriddenMethods(clazz);
        for (final Method declaredMethod : declaredMethodSet) {
            if (!containMethod(inheritedMethods, declaredMethod) && !containMethod(overriddenMethods, declaredMethod)) {
                ret.add(declaredMethod);
            }
        }

        return ret;
    }

    public static Set<Method> getInheritedMethods(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final Set<Method> declaredMethodSet = CollectionUtils.arrayToSet(methods);
        final Set<Method> ret = new HashSet<Method>(declaredMethodSet);

        Class<?> currentClass = clazz.getSuperclass();

        while (currentClass != null) {
            final Method[] superMethods = currentClass.getDeclaredMethods();
            for (Method superMethod : superMethods) {
                if (!Modifier.isPrivate(superMethod.getModifiers()) && !Modifier.isStatic(superMethod.getModifiers())
                        && !containMethod(ret, superMethod)) {
                    ret.add(superMethod);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        ret.removeAll(declaredMethodSet);

        return ret;
    }

    public static Set<Method> getOverriddenMethods(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final Set<Method> declaredMethodSet = CollectionUtils.arrayToSet(methods);
        final Set<Method> ret = new HashSet<>();

        Class<?> currentClass = clazz.getSuperclass();
        while (currentClass != null) {
            final Method[] superclassMethods = currentClass.getDeclaredMethods();
            for (Method superclassMethod : superclassMethods) {
                final Method match = getMatch(declaredMethodSet, superclassMethod, true);

                if (match != null) {
                    ret.add(match);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return ret;
    }

    public static boolean containField(final Set<Field> fields, final Field field) {
        for (final Field f : fields) {
            if (f.getName().equals(field.getName()) && f.getType().equals(field.getType())) {
                return true;
            }
        }

        return false;
    }

    public static boolean containMethod(final Set<Method> methods, final Method method) {
        for (final Method m : methods) {
            if (matchSignature(m, method) && matchModifier(m, method)) {
                return true;
            }
        }

        return false;
    }

    public static Field getMatch(final Set<Field> fields, final Field field) {
        for (final Field f : fields) {
            if (match(f, field)) {
                return f;
            }
        }

        return null;
    }

    public static Method getMatch(final Set<Method> methods, final Method maybeSuperclassMethod, final boolean matchInheritance) {
        for (final Method m : methods) {
            if (!matchInheritance) {
                if (matchModifier(m, maybeSuperclassMethod) && matchSignature(m, maybeSuperclassMethod)) {
                    return m;
                }
            } else {
                if (matchInheritance(m, maybeSuperclassMethod)) {
                    return m;
                }
            }
        }

        return null;
    }

    public static boolean match(final Field field1, final Field field2) {
        if (field1.getName().equals(field2.getName()) && field1.getType().equals(field2.getType())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean matchInheritance(final Field subclassField, final Field superclassField) {
        if (Modifier.isStatic(superclassField.getModifiers()) || subclassField.equals(superclassField)) {
            return false;
        }

        final Package subclassPackage = superclassField.getDeclaringClass().getPackage();
        final Package superclassPackage = superclassField.getDeclaringClass().getPackage();
        final int superFieldModifiers = superclassField.getModifiers();

        return isAccessable(subclassPackage, superclassPackage, superFieldModifiers);
    }

    public static boolean matchInheritance(final Method subclassMethod, final Method superclassMethod) {
        if (Modifier.isStatic(superclassMethod.getModifiers()) || subclassMethod.equals(superclassMethod)) {
            return false;
        }

        if (matchSignature(subclassMethod, superclassMethod)) {
            final Package subclassPackage = subclassMethod.getDeclaringClass().getPackage();
            final Package superclassPackage = superclassMethod.getDeclaringClass().getPackage();
            final int superMethodModifiers = superclassMethod.getModifiers();

            return isAccessable(subclassPackage, superclassPackage, superMethodModifiers);
        } else {
            return false;
        }
    }

    public static boolean matchModifier(final Method method1, final Method method2) {
        return method1.getModifiers() == method2.getModifiers();
    }

    public static boolean matchSignature(final Method method1, final Method method2) {
        return method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && hasSameParameterTypes(method1, method2);
    }

    public static boolean hasSameParameterTypes(final Method method1, final Method method2) {
        final Class<?>[] parameterTypes1 = method1.getParameterTypes();
        final Class<?>[] parameterTypes2 = method2.getParameterTypes();
        if (parameterTypes1.length == parameterTypes2.length) {
            for (int i = 0; i < parameterTypes1.length; i++) {
                if (!parameterTypes1[i].equals(parameterTypes2[i])) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public static Field getHideField(final Field superclassField, final Class<?> subclass) {
        final Class<?> superclass = superclassField.getDeclaringClass();
        Class<?> currentClass = subclass;

        if (!superclass.isAssignableFrom(subclass)) {
            throw new RuntimeException("Class[" + subclass + "] is not superclass" + " of class[" + subclass + "]");
        }

        while (!currentClass.equals(superclass)) {
            try {
                final Field m = currentClass.getDeclaredField(superclassField.getName());

                if (matchInheritance(m, superclassField)) {
                    return m;
                }
            } catch (final Exception ex) {
            }

            currentClass = currentClass.getSuperclass();
        }

        return superclassField;
    }

    public static Method getOverrideMethod(final Method superclassMethod, final Class<?> subclass) {
        final Class<?> superclass = superclassMethod.getDeclaringClass();
        Class<?> currentClass = subclass;

        if (!superclass.isAssignableFrom(subclass)) {
            throw new RuntimeException("Class[" + subclass + "] is not superclass" + " of class[" + subclass + "]");
        }

        while (!currentClass.equals(superclass)) {
            try {
                final Method m = currentClass.getDeclaredMethod(superclassMethod.getName(), superclassMethod.getParameterTypes());

                if (matchInheritance(m, superclassMethod)) {
                    return m;
                }
            } catch (final Exception ex) {
            }

            currentClass = currentClass.getSuperclass();
        }

        return superclassMethod;
    }
}
