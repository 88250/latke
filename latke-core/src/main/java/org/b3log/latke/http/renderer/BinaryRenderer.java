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
    private String contentType;

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
