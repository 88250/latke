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
package org.b3log.latke.servlet;

import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP request context.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 16, 2011
 */
public final class HTTPRequestContext {

    /**
     * Request.
     */
    private HttpServletRequest request;
    /**
     * Response.
     */
    private HttpServletResponse response;
    /**
     * Renderer.
     */
    private AbstractHTTPResponseRenderer renderer;

    /**
     * Gets the renderer.
     * 
     * @return renderer
     */
    public AbstractHTTPResponseRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the renderer with the specified renderer.
     * 
     * @param renderer the specified renderer
     */
    public void setRenderer(final AbstractHTTPResponseRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Gets the request.
     * 
     * @return request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Sets the request with the specified request.
     * 
     * @param request the specified request
     */
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Gets the response.
     * 
     * @return response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Sets the response with the specified response.
     * 
     * @param response the specified response
     */
    public void setResponse(final HttpServletResponse response) {
        this.response = response;
    }
}
