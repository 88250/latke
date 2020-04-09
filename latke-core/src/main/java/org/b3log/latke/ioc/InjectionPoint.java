/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.ioc;

import org.b3log.latke.ioc.annotated.Annotated;
import org.b3log.latke.ioc.annotated.AnnotatedField;

/**
 * Injection Point.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public interface InjectionPoint {

    /**
     * Get the {@link Bean} object representing the
     * bean that defines the injection point. If the injection point does not
     * belong to a bean, return a null value.
     *
     * @return the {@link Bean} object representing
     * bean that defines the injection point, of null if the injection
     * point does not belong to a bean
     */
    Bean<?> getBean();

    /**
     * Obtain an instance of {@link AnnotatedField}.
     *
     * @return an {@code AnnotatedField}
     */
    Annotated getAnnotated();
}
