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
package org.b3log.latke.http.renderer;

import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;

/**
 * HTTP 500 status renderer.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 2.0.0.0, Nov 3, 2019
 */
public final class Http500Renderer extends AbstractResponseRenderer {

    /**
     * The internal exception.
     */
    private Exception e;

    /**
     * Constructor.
     *
     * @param e internal exception
     */
    public Http500Renderer(final Exception e) {
        this.e = e;
    }

    @Override
    public void render(final RequestContext context) {
        final Response response = context.getResponse();
        response.sendError(500);
    }
}
