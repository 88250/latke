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

import org.b3log.latke.http.HttpMethod;
import org.b3log.latke.http.function.Handler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Context handler metadata.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 2.4.34
 */
public final class ContextHandlerMeta {

    /**
     * URI templates.
     */
    private String[] uriTemplates;

    /**
     * HTTP methods.
     */
    private HttpMethod[] httpMethods;

    /**
     * The processor method.
     */
    private Method invokeHolder;

    /**
     * Context handler.
     */
    private Handler handler;

    /**
     * Middlewares.
     */
    private List<Handler> middlewares;

    /**
     * Set the URI templates with the specified URI templates.
     *
     * @param uriTemplates the specified URI templates
     */
    public void setUriTemplates(final String[] uriTemplates) {
        this.uriTemplates = uriTemplates;
    }

    /**
     * Get the URI templates.
     *
     * @return URI templates
     */
    public String[] getUriTemplates() {
        return uriTemplates;
    }

    /**
     * Sets the HTTP methods with the specified HTTP methods.
     *
     * @param httpMethods httpMethods the specified HTTP methods
     */
    public void setHttpMethods(final HttpMethod[] httpMethods) {
        this.httpMethods = httpMethods;
    }

    /**
     * Get the HTTP methods.
     *
     * @return HTTP methods
     */
    public HttpMethod[] getHttpMethods() {
        return httpMethods;
    }

    /**
     * Sets the invoke holder with the specified invoke holder.
     *
     * @param invokeHolder invokeHolder the specified invoke holder
     */
    public void setInvokeHolder(final Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    /**
     * Gets the invoke holder.
     *
     * @return invoke holder
     */
    public Method getInvokeHolder() {
        return invokeHolder;
    }

    /**
     * Sets the context handler.
     *
     * @param handler the specified handler
     */
    public void setHandler(final Handler handler) {
        this.handler = handler;
    }

    /**
     * Gets the context handler.
     *
     * @return handler
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * Gets the middlewares.
     *
     * @return middlewares
     */
    public List<Handler> getMiddlewares() {
        return middlewares;
    }

    /**
     * Sets the middlewares.
     *
     * @param middlewares the specified middlewares
     */
    public void setMiddlewares(List<Handler> middlewares) {
        this.middlewares = middlewares;
    }
}
