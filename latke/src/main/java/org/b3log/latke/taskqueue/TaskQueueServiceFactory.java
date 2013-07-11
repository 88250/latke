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
package org.b3log.latke.taskqueue;


import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.logging.Logger;


/**
 * Task queue service factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Aug 27, 2012
 */
public final class TaskQueueServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TaskQueueServiceFactory.class.getName());

    /**
     * Task queue service.
     */
    private static final TaskQueueService TASK_QUEUE_SERVICE;

    static {
        LOGGER.info("Constructing Task Query Service....");

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        try {
            Class<TaskQueueService> serviceClass = null;

            switch (runtimeEnv) {
            case BAE:
            case LOCAL:
                serviceClass = (Class<TaskQueueService>) Class.forName("org.b3log.latke.taskqueue.local.LocalTaskQueueService");
                TASK_QUEUE_SERVICE = serviceClass.newInstance();
                break;

            case GAE:
                serviceClass = (Class<TaskQueueService>) Class.forName("org.b3log.latke.taskqueue.gae.GAETaskQueueService");
                TASK_QUEUE_SERVICE = serviceClass.newInstance();
                break;

            default:
                throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize Task Queue Service!", e);
        }

        LOGGER.info("Constructed Task Query Service");
    }

    /**
     * Gets task queue service.
     * 
     * @return task queue service
     */
    public static TaskQueueService getTaskQueueService() {
        return TASK_QUEUE_SERVICE;
    }

    /**
     * Private default constructor.
     */
    private TaskQueueServiceFactory() {}
}
