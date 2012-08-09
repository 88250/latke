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

import org.b3log.latke.taskqueue.TaskHandle;

/**
 * GAE task handle. 
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 15, 2011
 */
public final class GAETaskHandle implements TaskHandle {

    /**
     * GAE task handle.
     */
    private com.google.appengine.api.taskqueue.TaskHandle gaeTaskHandle;

    /**
     * Constructs a task handle with the specified GAE task handle.
     * 
     * @param gaeTaskHandle the specified GAE task handle
     */
    public GAETaskHandle(final com.google.appengine.api.taskqueue.TaskHandle gaeTaskHandle) {
        this.gaeTaskHandle = gaeTaskHandle;
    }

    @Override
    public long getEtaMillis() {
        return gaeTaskHandle.getEtaMillis();
    }

    @Override
    public String getQueueName() {
        return gaeTaskHandle.getQueueName();
    }

    @Override
    public int getRetriedCount() {
        final Integer retryCount = gaeTaskHandle.getRetryCount();
        if (null == retryCount) {
            return -1;
        }

        return retryCount;
    }

    @Override
    public String getTaskName() {
        return gaeTaskHandle.getName();
    }
}
