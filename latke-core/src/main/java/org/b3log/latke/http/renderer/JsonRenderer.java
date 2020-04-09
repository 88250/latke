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
import org.json.JSONObject;

/**
 * <a href="http://json.org">JSON</a> HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.1, Nov 11, 2019
 */
public final class JsonRenderer extends AbstractResponseRenderer {

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
        final Response response = context.getResponse();
        final int indent = 4;
        final String output = pretty ? jsonObject.toString(indent) : jsonObject.toString();
        if (!jsonp) {
            response.setContentType("application/json; charset=utf-8");
            response.sendString(output);
        } else {
            response.setContentType("application/javascript; charset=utf-8");
            response.sendString(callback + "(" + output + ")");
        }
    }
}
