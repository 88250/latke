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
 * Binary-like HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Nov 11, 2019
 */
public class BinaryRenderer extends AbstractResponseRenderer {

    /**
     * Content type.
     */
    private final String contentType;

    /**
     * Data.
     */
    private byte[] data;

    /**
     * Constructs a binary-like HTTP response renderer with the specified content type.
     *
     * @param contentType the specified content type
     */
    public BinaryRenderer(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the data with the specified data.
     *
     * @param data the specified data
     */
    public void setData(final byte[] data) {
        this.data = data;
    }

    @Override
    public void render(final RequestContext context) {
        final Response response = context.getResponse();
        response.setContentType(contentType);
        response.sendBytes(data);
    }
}
