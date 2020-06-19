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
package org.b3log.latke.http;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.function.Handler;
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
 * @version 2.2.0.0, Jun 19, 2020
 * @since 2.4.34
 */
public final class RequestContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RequestContext.class);

    /**
     * The key name of shared matched result while routing.
     */
    public static final String MATCH_RESULT = "MATCH_RESULT";

    /**
     * The key name of error code while handling error.
     */
    public static final String ERROR_CODE = "ERROR_CODE";

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
    private final List<Handler> handlers = new ArrayList<>();

    /**
     * Constructs a context with the specified request and response.
     *
     * @param request  the specified request
     * @param response the specified response
     */
    public RequestContext(final Request request, final Response response) {
        this.request = request;
        this.response = response;
    }

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

    public String requestQueryStr() {
        return request.getQueryString();
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
     * Sends the specified error status code and data model.
     *
     * @param sc        the specified error status code
     * @param dataModel the specified data model
     */
    public void sendError(final int sc, final Map<String, Object> dataModel) {
        response.sendError(sc, dataModel);
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
     * Renders with {"code": int, "data": JSONObject, "msg": ""}.
     *
     * @param code the specified code
     * @param data the specified data
     * @param msg  the specified msg
     * @return this context
     */
    public RequestContext renderCodeDataMsg(final int code, final JSONObject data, final String msg) {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;
            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.CODE, code).put(Keys.DATA, data).put(Keys.MSG, msg);
        }
        return this;
    }

    /**
     * Renders with {"code": int, "data": JSONObject}.
     *
     * @param code the specified code
     * @param data the specified data
     * @return this context
     */
    public RequestContext renderCodeData(final int code, final JSONObject data) {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;
            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.CODE, code).put(Keys.DATA, data);
        }
        return this;
    }

    /**
     * Renders with {"code": int, "msg": ""}.
     *
     * @param code the specified code
     * @param msg  the specified msg
     * @return this context
     */
    public RequestContext renderCodeMsg(final int code, final String msg) {
        if (renderer instanceof JsonRenderer) {
            final JsonRenderer r = (JsonRenderer) renderer;
            final JSONObject ret = r.getJSONObject();
            ret.put(Keys.CODE, code).put(Keys.MSG, msg);
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
     * Add the specified handlers to handler chain.
     *
     * @param handlers the specified handlers
     */
    public void addHandlers(final List<Handler> handlers) {
        this.handlers.addAll(handlers);
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
