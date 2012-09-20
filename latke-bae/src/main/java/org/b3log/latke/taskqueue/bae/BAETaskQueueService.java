/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.taskqueue.bae;

import com.baidu.bae.api.factory.BaeFactory;
import com.baidu.bae.api.taskqueue.BaeTaskQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.taskqueue.Queue;
import org.b3log.latke.taskqueue.Task;
import org.b3log.latke.taskqueue.TaskHandle;
import org.b3log.latke.taskqueue.TaskQueueService;

/**
 * Baidu App Engine task queue service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 20, 2012
 */
public final class BAETaskQueueService implements TaskQueueService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BAETaskQueueService.class.getName());

    @Override
    public Queue getQueue(final String queueName) {
        final BaeTaskQueue baeTaskQueue = BaeFactory.getBaeTaskQueue(queueName);

        return new Queue() {
            @Override
            public TaskHandle add(final Task task) {
                final com.baidu.bae.api.taskqueue.Task baeTask = new com.baidu.bae.api.taskqueue.FetchUrlTask(task.getURL(),
                        new String(task.getPayload()));

                baeTaskQueue.push(baeTask);

                LOGGER.log(Level.INFO, "Added a task[URL=" + task.getURL() + "] into task queue[name={0}]", queueName);

                return null;
            }
        };
    }
}
