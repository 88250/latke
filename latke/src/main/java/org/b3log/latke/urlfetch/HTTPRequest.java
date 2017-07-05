/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.b3log.latke.servlet.HTTPRequestMethod;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulation of a single HTTP request that is made via the {@link URLFetchService}.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.1, May 12, 2017
 */
public final class HTTPRequest {

    /**
     * Default connect timeout.
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 1000 * 2;

    /**
     * Default read timeout.
     */
    private static final int DEFAULT_READ_TIMEOUT = 1000 * 10;

    /**
     * URL.
     */
    private URL url;

    /**
     * Payload.
     */
    private byte[] payload;

    /**
     * Request method.
     */
    private HTTPRequestMethod requestMethod = HTTPRequestMethod.GET;

    /**
     * HTTP headers.
     */
    private List<HTTPHeader> headers = new ArrayList<HTTPHeader>();

    /**
     * Connect timeout in milliseconds.
     */
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * Read timeout in mmilliseconds.
     */
    private int readTimeout = DEFAULT_READ_TIMEOUT;

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
     * Gets the payload ({@link HTTPRequestMethod#POST POST} data body).
     * <p>
     * <p>
     * Certain HTTP methods ({@linkplain HTTPRequestMethod#GET GET}) will NOT have any payload, and this method will
     * return {@code null}.
     * </p>
     *
     * @return payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Sets the payload with the specified payload.
     * <p>
     * <p>
     * This method should NOT be called for certain HTTP methods (e.g. {@link HTTPRequestMethod#GET GET}).
     * </p>
     *
     * @param payload the specified payload
     */
    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

    /**
     * Gets the request method.
     *
     * @return request method
     */
    public HTTPRequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets the request method with the specified request method.
     *
     * @param requestMethod the specified request method
     */
    public void setRequestMethod(final HTTPRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Gets the request URL.
     *
     * @return request URL
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the request URL with the specified URL.
     *
     * @param url the specified URL
     */
    public void setURL(final URL url) {
        this.url = url;
    }

    /**
     * Gets the connect timeout.
     *
     * @return connect timeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout with the specified connect timeout.
     *
     * @param connectTimeout the specified connect timeout
     */
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Gets the read timeout.
     *
     * @return read timeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the read timeout with the specified read timeout.
     *
     * @param readTimeout the specified read timeout
     */
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
