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
package org.b3log.latke.ioc;

import org.b3log.latke.ioc.config.BeanModule;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.ApplicationContext;
import org.b3log.latke.ioc.context.Context;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * The Latke bean lifecycle functions facade.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Sep 29, 2018
 * @since 2.4.18
 */
public final class Lifecycle {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Lifecycle.class);

    /**
     * Latke bean manager.
     */
    private static BeanManager beanManager;

    /**
     * Application context.
     */
    private static ApplicationContext applicationContext = new ApplicationContext();

    /**
     * Private constructor.
     */
    private Lifecycle() {
    }

    /**
     * Starts the application with the specified bean class and bean modules.
     *
     * @param classes    the specified bean class, nullable
     * @param beanModule the specified bean modules
     */
    public static void startApplication(final Collection<Class<?>> classes, final BeanModule... beanModule) {
        LOGGER.log(Level.DEBUG, "Initializing Latke IoC container");

        beanManager = BeanManager.getInstance();
        applicationContext.setActive(true);
        beanManager.addContext(applicationContext);
        final Configurator configurator = beanManager.getConfigurator();
        if (null != classes && !classes.isEmpty()) {
            configurator.createBeans(classes);
        }

        if (null != beanModule && 0 < beanModule.length) {
            for (int i = 0; i < beanModule.length; i++) {
                configurator.addModule(beanModule[i]);
            }
        }

        LOGGER.log(Level.DEBUG, "Initialized Latke IoC container");
    }

    /**
     * Ends the application.
     */
    public static void endApplication() {
        final ApplicationContext applicationCxt = getApplicationContext();
        applicationCxt.destroy();
        beanManager.clearContexts();

        LOGGER.log(Level.DEBUG, "Latke IoC container ended");
    }

    /**
     * Gets the application context.
     *
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets bean manager.
     *
     * @return bean manager
     */
    public static BeanManager getBeanManager() {
        return beanManager;
    }
}
