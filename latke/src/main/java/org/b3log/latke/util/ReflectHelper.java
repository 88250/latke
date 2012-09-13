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
package org.b3log.latke.util;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * ReflectHelper while not using java reflect instead of the other class byte tool.
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 10, 2012
 */
public final class ReflectHelper {

    /**
     * the defalut constructor.
     */
    private ReflectHelper() {
    }

    /**
     * getMethodVariableNames in user defined.
     * @param clazz the specific clazz
     * @param targetMethodName the targetMethodName
     * @param types the types of the method
     * @return the String[] of names
     */
    public static String[] getMethodVariableNames(final Class<?> clazz, final String targetMethodName, final Class<?>[] types) {

        final ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(clazz));
        CtClass cc;
        CtMethod cm = null;
        try {
            cc = pool.get(clazz.getName());
            final CtClass[] ptypes = new CtClass[types.length];
            for (int i = 0; i < ptypes.length; i++) {
                ptypes[i] = pool.get(types[i].getName());
            }
            cm = cc.getDeclaredMethod(targetMethodName, ptypes);
        } catch (final NotFoundException e) {
            e.printStackTrace();
        }

        final MethodInfo methodInfo = cm.getMethodInfo();
        final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        final LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] variableNames = new String[0];
        try {
            variableNames = new String[cm.getParameterTypes().length];
        } catch (final NotFoundException e) {
            e.printStackTrace();
        }
        final int staticIndex = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < variableNames.length; i++) {
            variableNames[i] = attr.variableName(i
                    + staticIndex);

        }
        return variableNames;
    }

}
