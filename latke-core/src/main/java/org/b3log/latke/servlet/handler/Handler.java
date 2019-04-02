/*
 * Copyright (c) 2009-present, b3log.org
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
package org.b3log.latke.servlet.handler;

import org.b3log.latke.servlet.RequestContext;

/**
 * A handler interface for  {@link org.b3log.latke.servlet.DispatcherServlet} to do the inner process.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 5, 2018
 * @since 2.4.34
 */
public interface Handler {

    /**
     * Handle.
     *
     * @param context the specified request context
     * @throws Exception exception
     */
    void handle(final RequestContext context) throws Exception;
}
