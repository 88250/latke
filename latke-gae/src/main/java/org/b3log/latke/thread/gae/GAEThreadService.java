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
package org.b3log.latke.thread.gae;

import com.google.appengine.api.ThreadManager;
import org.b3log.latke.thread.ThreadService;

/**
 * Google App Engine thread service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 20, 2012
 */
public final class GAEThreadService implements ThreadService {

    @Override
    public Thread createThreadForCurrentRequest(final Runnable runnable) {
        return ThreadManager.createThreadForCurrentRequest(runnable);
    }
}
