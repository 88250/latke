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
package org.b3log.latke.http.handler;

import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.http.renderer.StaticFileRenderer;

/**
 * Static resource handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.0.0.0, Nov 3, 2019
 */
public class StaticResourceHandler implements Handler {

    @Override
    public void handle(final RequestContext context) {
        final Request request = context.getRequest();
        if (request.isStaticResource()) {
            context.setRenderer(new StaticFileRenderer());
            context.abort();

            return;
        }

        context.handle();
    }
}
