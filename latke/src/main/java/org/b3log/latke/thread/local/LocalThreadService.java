/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.thread.local;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.thread.ThreadService;


/**
 * Local thread service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 10, 2014
 */
public final class LocalThreadService implements ThreadService {
    
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalThreadService.class);

    /**
     * Executor service.
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    @Override
    public Thread createThreadForCurrentRequest(final Runnable runnable) {
        return Executors.defaultThreadFactory().newThread(runnable);
    }

    @Override
    public Future<?> submit(final Runnable runnable, final long millseconds) {
        try {
            final Future<?> ret = executorService.submit(runnable);
            
            ret.get(millseconds, TimeUnit.MILLISECONDS);
            
            return ret;
        } catch (final RejectedExecutionException  e) {
            LOGGER.log(Level.ERROR, "Task executes failed", e);
            
            return null;
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Task executes timeout", e);
            
            return null;
        }
    }
}
