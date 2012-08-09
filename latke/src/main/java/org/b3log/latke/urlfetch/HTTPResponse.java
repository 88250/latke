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
package org.b3log.latke.urlfetch;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulation of the result of a {@link HTTPRequest HTTP request} made via 
 * the {@link URLFetchService}. 
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public final class HTTPResponse {

    /**
     * Content.
     */
    private byte[] content;
    /**
     * Final URL.
     */
    private URL finalURL;
    /**
     * Response code.
     */
    private int responseCode;
    /**
     * HTTP headers.
     */
    private List<HTTPHeader> headers = new ArrayList<HTTPHeader>();

    /**
     * Adds the specified HTTP header.
     * 
     * @param header the specified HTTP header
     */
    public void addHeader(final HTTPHeader header) {
        headers.add(header);
    }

    /**
     * Gets HTTP headers.
     * 
     * @return HTTP headers
     */
    public List<HTTPHeader> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    /**
     * Gets the content.
     * 
     * @return content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content with the specified content.
     * 
     * @param content the specified content
     */
    public void setContent(final byte[] content) {
        this.content = content;
    }

    /**
     * Gets the final URL.
     * 
     * @return final URL
     */
    public URL getFinalURL() {
        return finalURL;
    }

    /**
     * Sets the final URL with the specified final URL.
     * 
     * @param finalURL the specified final URL
     */
    public void setFinalURL(final URL finalURL) {
        this.finalURL = finalURL;
    }

    /**
     * Gets the response code.
     * 
     * @return response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the response code with the specified response code.
     * 
     * @param responseCode the specified response code
     */
    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }
}
