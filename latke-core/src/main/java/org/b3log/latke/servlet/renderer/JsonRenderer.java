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
package org.b3log.latke.servlet.renderer;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <a href="http://json.org">JSON</a> HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.1, Mar 5, 2017
 */
public final class JsonRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JsonRenderer.class);

    /**
     * Pretty output.
     */
    private boolean pretty;

    /**
     * JSON object to render.
     */
    private JSONObject jsonObject;

    /**
     * Determines whether render as JSONP.
     */
    private boolean jsonp;

    /**
     * JSONP callback function name.
     */
    private String callback = "callback";

    /**
     * Determins whether pretty rendering.
     *
     * @return {@code true} for pretty rendering, returns {@code false} otherwise
     */
    public boolean isPretty() {
        return pretty;
    }

    /**
     * Sets whether pretty rendering.
     *
     * @param pretty {@code true} for pretty rendering, {@code false} otherwise
     */
    public void setPretty(final boolean pretty) {
        this.pretty = pretty;
    }

    /**
     * Gets the json object to render.
     *
     * @return the json object
     */
    public JSONObject getJSONObject() {
        return jsonObject;
    }

    /**
     * Sets the json object to render with the specified json object.
     *
     * @param jsonObject the specified json object
     */
    public void setJSONObject(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Determines whether render as JSONP.
     *
     * @return {@code true} for JSONP, {@code false} otherwise
     */
    public boolean isJSONP() {
        return jsonp;
    }

    /**
     * Sets whether render as JSONP.
     *
     * @param isJSONP {@code true} for JSONP, {@code false} otherwise
     * @return this
     */
    public JsonRenderer setJSONP(final boolean isJSONP) {
        this.jsonp = isJSONP;

        return this;
    }

    /**
     * Sets JSONP callback function.
     * <p>
     * Invokes this method will set {@link #isJSONP} to {@code true}
     * automatically.
     * </p>
     *
     * @param callback the specified callback function name
     */
    public void setCallback(final String callback) {
        this.callback = callback;

        setJSONP(true);
    }

    @Override
    public void render(final RequestContext context) {
        final HttpServletResponse response = context.getResponse();
        response.setCharacterEncoding("UTF-8");

        try {
            final PrintWriter writer = response.getWriter();

            final int indent = 4;
            final String output = pretty ? jsonObject.toString(indent) : jsonObject.toString();

            if (!jsonp) {
                response.setContentType("application/json");
                writer.println(output);
            } else {
                response.setContentType("application/javascript");
                writer.print(callback + "(" + output + ")");
            }

            writer.close();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "FreeMarker renders error", e);

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (final IOException ex) {
                LOGGER.log(Level.ERROR, "Can not send error 500!", ex);
            }
        }
    }
}
