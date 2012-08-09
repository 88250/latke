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
package org.b3log.latke.taskqueue.local;

import org.b3log.latke.taskqueue.TaskHandle;

/**
 * Local task queue service.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Apr 5, 2012
 */
public class LocalTaskhandle implements TaskHandle {

    @Override
    public long getEtaMillis() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getQueueName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRetriedCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getTaskName() {
        // TODO Auto-generated method stub
        return null;
    }

}
