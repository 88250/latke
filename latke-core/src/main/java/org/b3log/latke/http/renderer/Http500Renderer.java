/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.http.renderer;

import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;

/**
 * HTTP 500 status renderer.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @version 2.0.0.0, Nov 3, 2019
 */
public final class Http500Renderer extends AbstractResponseRenderer {

    /**
     * The internal exception.
     */
    private final Exception e;

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
