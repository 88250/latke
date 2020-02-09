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
package org.b3log.latke.http;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.handler.Handler;
import org.b3log.latke.http.renderer.AbstractResponseRenderer;
import org.b3log.latke.http.renderer.Http500Renderer;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.util.Requests;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP request context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.0.0, Feb 9, 2020
 * @since 2.4.34
 */
public final class RequestContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RequestContext.class);

    /**
     * Request.
     */
    private Request request;

    /**
     * Response.
     */
    private Response response;

    /**
     * Renderer.
     */
    private AbstractResponseRenderer renderer;

    /**
     * Path vars.
     */
    private Map<String, String> pathVars = new HashMap<>();

    /**
     * Process flow index.
     */
    private int handleIndex = -1;

    /**
     * Handlers.
     */
    private List<Handler> handlers = new ArrayList<>();

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
    public Request getRequest() {
        return request;
    }

    /**
     * Sets the request with the specified request.
     *
     * @param request the specified request
     */
    public void setRequest(final Request request) {
        this.request = request;
    }

    /**
     * Gets the response.
     *
     * @return response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the response with the specified response.
     *
     * @param response the specified response
     */
    public void setResponse(final Response response) {
        this.response = response;
    }

    /**
     * Gets the data model of renderer bound with this context.
     *
     * @return data model, returns {@code null} if not found
     */
    public Map<String, Object> getDataModel() {
        final AbstractResponseRenderer renderer = getRenderer();
        if (null == renderer) {
            return null;
        }

        return renderer.getRenderDataModel();
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
        try {
            return request.getParameter(name);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Can't parse request parameter [uri=" + request.getRequestURI() + ", method=" + request.getMethod() + ", parameterName=" + name + "]: " + e.getMessage());

            return null;
        }
    }

    public byte[] requestBytes() {
        return request.getBytes();
    }

    public String requestString() {
        return request.getString();
    }

    /**
     * Gets a path var specified by the given name from request URI.
     *
     * @param name the given name
     * @return path var, returns {@code null} if not found
     */
    public String pathVar(final String name) {
        return pathVars.get(name);
    }

    /**
     * Get all path vars.
     *
     * @return path vars
     */
    public Map<String, String> pathVars() {
        return pathVars;
    }

    /**
     * Set all path vars.
     *
     * @param pathVars the specified path vars
     */
    public void pathVars(final Map<String, String> pathVars) {
        this.pathVars = pathVars;
    }

    /**
     * Puts a path var specified by the given name and value.
     *
     * @param name  the given name
     * @param value the given value
     */
    public void pathVar(final String name, String value) {
        pathVars.put(name, value);
    }

    /**
     * Sends the specified error status code.
     *
     * @param sc the specified error status code
     */
    public void sendError(final int sc) {
        response.sendError(sc);
    }

    /**
     * Sends the specified status code.
     *
     * @param sc the specified status code
     */
    public void sendStatus(final int sc) {
        response.setStatus(sc);
        response.send();
    }

    /**
     * Sends redirect to the specified location.
     *
     * @param location the specified location
     */
    public void sendRedirect(final String location) {
        try {
            response.sendRedirect(new URI(location).toASCIIString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends redirect [" + location + "] failed: " + e.getMessage());
        }
    }

    /**
     * Sends the specified bytes.
     *
     * @param bytes the specified bytes
     */
    public void sendBytes(final byte[] bytes) {
        response.sendBytes(bytes);
    }

    /**
     * Sends the specified string.
     *
     * @param string the specified string
     */
    public void sendString(final String string) {
        response.sendString(string);
    }

    /**
     * Sends the specified status code.
     *
     * @param sc the specified status code
     */
    public void setStatus(final int sc) {
        response.setStatus(sc);
    }

    /**
     * Gets the request json object.
     *
     * @return request json object
     */
    public JSONObject requestJSON() {
        return request.getJSON();
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
     * Renders using {@link JsonRenderer} with {"code": int}.
     *
     * @param code the specified code
     * @return this context
     */
    public RequestContext renderJSON(final int code) {
        final JsonRenderer jsonRenderer = new JsonRenderer();
        final JSONObject ret = new JSONObject().put(Keys.CODE, code);
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
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

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
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.STATUS_CODE, false);
        }

        return this;
    }

    /**
     * Renders with {"code": int}.
     *
     * @param code the specified code
     * @return this context
     */
    public RequestContext renderCode(final int code) {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.CODE, code);
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
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.MSG, msg);
        }

        return this;
    }

    /**
     * Renders with {"data": obj}.
     *
     * @param data the specified JSON data
     * @return this context
     */
    public RequestContext renderData(final Object data) {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.DATA, data);
        }

        return this;
    }

    /**
     * Pretty renders.
     *
     * @return this context
     */
    public RequestContext renderPretty() {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;
            r.setPretty(true);
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
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;

            final JSONObject ret = r.getJSONObject();
            ret.put(name, obj);
        }

        return this;
    }

    /**
     * Handles this context with handlers.
     */
    public void handle() {
        try {
            for (handleIndex++; handleIndex < handlers.size(); handleIndex++) {
                handlers.get(handleIndex).handle(this);
            }
        } catch (final Exception e) {
            final String requestLog = Requests.getLog(request);
            LOGGER.log(Level.ERROR, "Handler process failed: " + requestLog, e);

            setRenderer(new Http500Renderer(e));
        }
    }

    /**
     * Aborts the remaining handlers.
     */
    public void abort() {
        handleIndex = 64;
    }

    /**
     * Adds the specified handler to handler chain.
     *
     * @param handler the specified handler
     */
    public void addHandler(final Handler handler) {
        handlers.add(handler);
    }

    /**
     * Inserts the specified handler after the current handler.
     *
     * @param handler the specified handler
     */
    public void insertHandlerAfter(final Handler handler) {
        handlers.add(handleIndex + 1, handler);
    }
}
