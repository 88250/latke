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
package org.b3log.latke.thread;

import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;

/**
 * Thread service factory.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 20, 2012
 */
public final class ThreadServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ThreadServiceFactory.class.getName());
    /**
     * Thread service.
     */
    private static final ThreadService THREAD_SERVICE;

    static {
        LOGGER.info("Constructing Thread Service....");

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        try {
            Class<ThreadService> serviceClass = null;

            switch (runtimeEnv) {
                case BAE:
                    serviceClass = (Class<ThreadService>) Class.forName("org.b3log.latke.thread.bae.BAEThreadService");
                    THREAD_SERVICE = serviceClass.newInstance();
                    break;
                case LOCAL:
                    serviceClass = (Class<ThreadService>) Class.forName("org.b3log.latke.thread.local.LocalThreadService");
                    THREAD_SERVICE = serviceClass.newInstance();
                    break;
                case GAE:
                    serviceClass = (Class<ThreadService>) Class.forName("org.b3log.latke.thread.gae.GAEThreadService");
                    THREAD_SERVICE = serviceClass.newInstance();
                    break;
                default:
                    throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize Thread Service!", e);
        }

        LOGGER.info("Constructed Thread Service");
    }

    /**
     * Gets thread service.
     *
     * @return thread service
     */
    public static ThreadService getThreadService() {
        return THREAD_SERVICE;
    }

    /**
     * Private default constructor.
     */
    private ThreadServiceFactory() {
    }
}
