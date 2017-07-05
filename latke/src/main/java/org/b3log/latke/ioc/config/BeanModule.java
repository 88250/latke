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
package org.b3log.latke.ioc.config;


import java.util.Collection;


/**
 * Module.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jun 20, 2013
 */
public final class BeanModule {
    
    /**
     * Name of this module.
     */
    private String name;

    /**
     * Bean classes of this module.
     */
    private Collection<Class<?>> beanClasses;

    /**
     * Constructs a module with the specified module name and the specified bean classes.
     * 
     * @param name the specified module name
     * @param beanClasses the specified bean classes
     */
    public BeanModule(final String name, final Collection<Class<?>> beanClasses) {
        this.name = name;
        this.beanClasses = beanClasses;
    }
    
    /**
     * Gets name of this module.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the bean classes of this module.
     * 
     * @return bean classes
     */
    public Collection<Class<?>> getBeanClasses() {
        return beanClasses;
    }
}
