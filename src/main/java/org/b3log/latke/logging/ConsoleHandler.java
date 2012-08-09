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
package org.b3log.latke.logging;

import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

/**
 * JDK log console handler.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 5, 2010
 */
public final class ConsoleHandler extends java.util.logging.ConsoleHandler {

    /**
     * Public default constructor.
     */
    public ConsoleHandler() {
        super(); // XXX: security checking, sealed
        final String propName = getClass().getName() + ".formatter";

        final Formatter formatter = getFormatter(propName);
        setFormatter(formatter);
    }

    /**
     * Gets formatter in configuration.
     *
     * @param name formatter property name
     * @return the configured formatter instance, returns a
     * {@link SimpleFormatter} instance if not found the configured formatter
     */
    private Formatter getFormatter(final String name) {
        final String val = LogManager.getLogManager().getProperty(name);
        try {
            if (val != null) {
                final Class<?> clz = Class.forName(val);
                return (Formatter) clz.newInstance();
            }
        } catch (final Exception ex) {
           throw new RuntimeException(ex);
        }

        return new SimpleFormatter();
    }
}
