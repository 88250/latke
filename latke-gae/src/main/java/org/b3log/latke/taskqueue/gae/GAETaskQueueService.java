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
package org.b3log.latke.taskqueue.gae;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.taskqueue.Queue;
import org.b3log.latke.taskqueue.Task;
import org.b3log.latke.taskqueue.TaskHandle;
import org.b3log.latke.taskqueue.TaskQueueService;

/**
 * Task.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 21, 2012
 */
public final class GAETaskQueueService implements TaskQueueService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GAETaskQueueService.class.getName());

    @Override
    public Queue getQueue(final String queueName) {
        final com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getQueue(queueName);

        return new Queue() {

            @Override
            public TaskHandle add(final Task task) {
                final TaskOptions taskOptions = TaskOptions.Builder.withTaskName(task.getName()).url(task.getURL());
                final HTTPRequestMethod requestMethod = task.getRequestMethod();

                switch (requestMethod) {
                    case GET:
                        taskOptions.method(TaskOptions.Method.GET);
                        break;
                    case DELETE:
                        taskOptions.method(TaskOptions.Method.DELETE);
                        break;
                    case HEAD:
                        taskOptions.method(TaskOptions.Method.HEAD);
                        break;
                    case POST:
                        taskOptions.method(TaskOptions.Method.POST);
                        break;
                    case PUT:
                        taskOptions.method(TaskOptions.Method.PUT);
                        break;
                    default:
                        LOGGER.log(Level.WARNING, "Task request method[{0}], uses GET method instead", requestMethod);
                        taskOptions.method(TaskOptions.Method.GET);
                        break;
                }

                final com.google.appengine.api.taskqueue.TaskHandle handle = queue.add(taskOptions);
                final TaskHandle ret = new GAETaskHandle(handle);

                return ret;
            }
        };
    }
}
