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
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
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
public final class HTTPRequestContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HTTPRequestContext.class);

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
    public HTTPRequestContext renderJSONPretty(final JSONObject json) {
        final JSONRenderer jsonRenderer = new JSONRenderer();
        jsonRenderer.setJSONObject(json);
        jsonRenderer.setPretty(true);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders using {@link JSONRenderer} with {"sc": false}.
     *
     * @return this context
     */
    public HTTPRequestContext renderJSON() {
        final JSONRenderer jsonRenderer = new JSONRenderer();
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
    public HTTPRequestContext renderJSON(final JSONObject json) {
        final JSONRenderer jsonRenderer = new JSONRenderer();
        jsonRenderer.setJSONObject(json);

        this.renderer = jsonRenderer;

        return this;
    }

    /**
     * Renders using {@link JSONRenderer} with {"sc": sc}.
     *
     * @param sc the specified sc
     * @return this context
     */
    public HTTPRequestContext renderJSON(final boolean sc) {
        final JSONRenderer jsonRenderer = new JSONRenderer();
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
    public HTTPRequestContext renderTrueResult() {
        if (this.renderer instanceof JSONRenderer) {
            final JSONRenderer r = (JSONRenderer) this.renderer;

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
    public HTTPRequestContext renderFalseResult() {
        if (this.renderer instanceof JSONRenderer) {
            final JSONRenderer r = (JSONRenderer) this.renderer;

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
    public HTTPRequestContext renderMsg(final String msg) {
        if (this.renderer instanceof JSONRenderer) {
            final JSONRenderer r = (JSONRenderer) this.renderer;

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
    public HTTPRequestContext renderJSONValue(final String name, final Object obj) {
        if (this.renderer instanceof JSONRenderer) {
            final JSONRenderer r = (JSONRenderer) this.renderer;

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
