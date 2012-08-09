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
package org.b3log.latke.servlet.renderer;

import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.servlet.HTTPRequestContext;

/**
 * <a href="http://json.org">JSON</a> HTTP response renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 20, 2011
 */
public final class JSONRenderer extends AbstractHTTPResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JSONRenderer.class.getName());
    /**
     * JSON object to render.
     */
    private JSONObject jsonObject;
    /**
     * Determines whether render as JSONP.
     */
    private boolean isJSONP;
    /**
     * JSONP callback function name.
     */
    private String callback = "callback";

    /**
     * Sets the json object to render with the specified json object.
     * 
     * @param jsonObject the specified json object
     */
    public void setJSONObject(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Sets whether render as JSONP.
     * 
     * @param isJSONP {@code true} for JSONP, {@code false} otherwise
     * @return this 
     */
    public JSONRenderer setJSONP(final boolean isJSONP) {
        this.isJSONP = isJSONP;

        return this;
    }

    /**
     * Sets JSONP callback function.
     * 
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
    public void render(final HTTPRequestContext context) {
        final HttpServletResponse response = context.getResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            final PrintWriter writer = response.getWriter();

            if (!isJSONP) {
                writer.println(jsonObject);
            } else {
                writer.print(callback + "(" + jsonObject + ")");
            }

            writer.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "FreeMarker renders error", e);

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (final IOException ex) {
                LOGGER.log(Level.SEVERE, "Can not send error 500!", ex);
            }
        }
    }
}
