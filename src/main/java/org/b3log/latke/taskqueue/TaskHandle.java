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
package org.b3log.latke.taskqueue;

/**
 * Task handle. 
 * 
 * <p>
 * Created from {@link Queue#add(org.b3log.latke.taskqueue.Task)}, for a task
 * in a task queue.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 15, 2011
 */
public interface TaskHandle {

    /**
     * Gets the time from task scheduled to now.
     * 
     * @return time from task scheduled to now
     */
    long getEtaMillis();

    /**
     * Gets the queue name.
     * 
     * @return queue name
     */
    String getQueueName();

    /**
     * Gets the retried count.
     * 
     * @return retried count, returns {@code -1} if has not been performed yet
     */
    int getRetriedCount();

    /**
     * Gets task name.
     * 
     * @return task name
     */
    String getTaskName();
}
