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
package org.b3log.latke.ioc.point;


import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.b3log.latke.ioc.annotated.Annotated;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.util.Beans;


/**
 * Abstract injection point.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 16, 2009
 */
public abstract class AbstractInjectionPoint implements InjectionPoint {

    /**
     * Annotated element.
     */
    private Annotated annotated;

    /**
     * Owner bean.
     */
    private Bean<?> ownerBean;

    /**
     * Constructs a injection point with the specified arguments.
     * 
     * @param ownerBean the specified owner bean
     * @param annotated the specified annotated element
     */
    public AbstractInjectionPoint(final Bean<?> ownerBean, final Annotated annotated) {
        this.ownerBean = ownerBean;
        this.annotated = annotated;
    }

    @Override
    public Type getType() {
        final Type baseType = annotated.getBaseType();

        if (baseType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) baseType;

            return parameterizedType.getActualTypeArguments()[0];
        } else {
            return baseType;
        }
    }

    @Override
    public Set<Annotation> getQualifiers() {
        final Set<Annotation> annotations = annotated.getAnnotations();
        final Set<Annotation> ret = Beans.selectQualifiers(annotations);

        return ret == null ? new HashSet<Annotation>() : ret;
    }

    @Override
    public Bean<?> getBean() {
        return ownerBean;
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public boolean isDelegate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTransient() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
