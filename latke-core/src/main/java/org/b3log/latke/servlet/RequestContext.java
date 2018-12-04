/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.renderer.AbstractResponseRenderer;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.latke.util.Requests;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * HTTP request context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.0, Dec 3, 2018
 */
public final class RequestContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestContext.class);

    /**
     * Request.
     */
    private HttpServletRequest request;

    /**
     * Response.
     */
    private HttpServletResponse response;

    /**
     * Request json.
     */
    private JSONObject requestJSON;

    /**
     * Renderer.
     */
    private AbstractResponseRenderer renderer;

    /**
     * Gets the renderer.
     *
     * @return renderer
     */
    public AbstractResponseRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the renderer with the specified renderer.
     *
     * @param renderer the specified renderer
     */
    public void setRenderer(final AbstractResponseRenderer renderer) {
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

    /**
     * Gets the remote address of request.
     *
     * @return remote address
     */
    public String remoteAddr() {
        return Requests.getRemoteAddr(request);
    }

    /**
     * Sends redirect to the specified location.
     *
     * @param location the specified location
     */
    public void sendRedirect(final String location) {
        try {
            response.sendRedirect(location);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends redirect [" + location + "] failed: " + e.getMessage());
        }
    }

    /**
     * Gets the value of a header specified by the given name
     *
     * @param name the given name
     */
    public String header(final String name) {
        return request.getHeader(name);
    }

    /**
     * Adds a header specified by the given name and value to the response.
     *
     * @param name  the given name
     * @param value the given value
     */
    public void addHeader(final String name, final String value) {
        response.addHeader(name, value);
    }

    /**
     * Sets a header specified by the given name and value to the response.
     *
     * @param name  the given name
     * @param value the given value
     */
    public void setHeader(final String name, final String value) {
        response.setHeader(name, value);
    }

    /**
     * Gets the request HTTP method.
     *
     * @return HTTP method
     */
    public String method() {
        return request.getMethod();
    }

    /**
     * Gets the request URI.
     *
     * @return request URI
     */
    public String requestURI() {
        return request.getRequestURI();
    }

    /**
     * Gets an attribute specified by the given name from request.
     *
     * @param name the given name
     * @return attribute, returns {@code null} if not found
     */
    public Object attr(final String name) {
        return request.getAttribute(name);
    }

    /**
     * Sets an attribute specified by the given name and value into request.
     *
     * @param name  the given name
     * @param value the given value
     */
    public void attr(final String name, final Object value) {
        request.setAttribute(name, value);
    }

    /**
     * Gets a parameter specified by the given name from request body form or query string.
     *
     * @param name the given name
     * @return parameter, returns {@code null} if not found
     */
    public String param(final String name) {
        return request.getParameter(name);
    }

    /**
     * Gets a path var specified by the given name from request URI.
     *
     * @param name the given name
     * @return path var, returns {@code null} if not found
     */
    public String pathVar(final String name) {
        return null;
    }

    /**
     * Sends the specified error status code.
     *
     * @param sc the specified error status code
     */
    public void sendError(final int sc) {
        try {
            response.sendError(sc);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends error status code [" + sc + "] failed: " + e.getMessage());
        }
    }

    /**
     * Sends the specified status code.
     *
     * @param sc the specified status code
     */
    public void setStatus(final int sc) {
        try {
            response.setStatus(sc);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends status code [" + sc + "] failed: " + e.getMessage());
        }
    }

    /**
     * Parses request body into a json object.
     *
     * @return parsed request json object
     */
    public JSONObject requestJSON() {
        if (null == requestJSON) {
            requestJSON = parseRequestJSONObject(request, response);
        }

        return requestJSON;
    }

    /**
     * Pretty rends with the specified json object.
     *
     * @param json the specified json object
     * @return this context
     */
    public RequestContext renderJSONPretty(final JSONObject json) {
        final JsonRenderer jsonRenderer = new JsonRenderer();
        jsonRenderer.setJSONObject(json);
        jsonRenderer.setPretty(true);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders using {@link JsonRenderer} with {"sc": false}.
     *
     * @return this context
     */
    public RequestContext renderJSON() {
        final JsonRenderer jsonRenderer = new JsonRenderer();
        final JSONObject ret = new JSONObject().put(Keys.STATUS_CODE, false);
        jsonRenderer.setJSONObject(ret);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders with the specified json object.
     *
     * @param json the specified json object
     * @return this context
     */
    public RequestContext renderJSON(final JSONObject json) {
        final JsonRenderer jsonRenderer = new JsonRenderer();
        jsonRenderer.setJSONObject(json);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders using {@link JsonRenderer} with {"sc": sc}.
     *
     * @param sc the specified sc
     * @return this context
     */
    public RequestContext renderJSON(final boolean sc) {
        final JsonRenderer jsonRenderer = new JsonRenderer();
        final JSONObject ret = new JSONObject().put(Keys.STATUS_CODE, sc);
        jsonRenderer.setJSONObject(ret);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders with {"sc": true}.
     *
     * @return this context
     */
    public RequestContext renderTrueResult() {
        if (this.renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) this.renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.STATUS_CODE, true);
        }

        return this;
    }

    /**
     * Renders with {"sc": false}.
     *
     * @return this context
     */
    public RequestContext renderFalseResult() {
        if (this.renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) this.renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.STATUS_CODE, false);
        }

        return this;
    }

    /**
     * Renders with {"msg": msg}.
     *
     * @param msg the specified msg
     * @return this context
     */
    public RequestContext renderMsg(final String msg) {
        if (this.renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) this.renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.MSG, msg);
        }

        return this;
    }

    /**
     * Renders with {"name", obj}.
     *
     * @param name the specified name
     * @param obj  the specified object
     * @return this context
     */
    public RequestContext renderJSONValue(final String name, final Object obj) {
        if (this.renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) this.renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(name, obj);
        }

        return this;
    }

    /**
     * Gets the request json object with the specified request.
     *
     * @param request  the specified request
     * @param response the specified response, sets its content type with "application/json"
     * @return a json object
     */
    private static JSONObject parseRequestJSONObject(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("application/json");

        try {
            BufferedReader reader;
            try {
                reader = request.getReader();
            } catch (final IllegalStateException illegalStateException) {
                reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            }

            String tmp = IOUtils.toString(reader);
            if (StringUtils.isBlank(tmp)) {
                tmp = "{}";
            }

            return new JSONObject(tmp);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Parses request JSON object failed [" + e.getMessage() + "], returns an empty json object");

            return new JSONObject();
        }
    }
}
