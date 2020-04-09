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
package org.b3log.latke.ioc.simplest;

import org.b3log.latke.ioc.Bean;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;

import java.util.Arrays;
import java.util.List;

@Singleton
public class Main {

    @Inject
    private DependencyA dependencyA;

    /**
     * Classes package.
     */
    public static final List<Class<?>> packageClasses = Arrays.asList(Main.class, DependencyA.class);

    public static void main(String[] args) {
        BeanManager.start(packageClasses);

        final BeanManager beanManager = BeanManager.getInstance();
        final Bean bean = beanManager.getBean(Main.class);
        final Main main = (Main) beanManager.getReference(bean);

        main.dependencyA.method1();
    }
}
