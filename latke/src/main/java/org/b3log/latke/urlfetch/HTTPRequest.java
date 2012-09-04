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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.b3log.latke.servlet.HTTPRequestMethod;

/**
 * Encapsulation of a single HTTP request that is made via the 
 * {@link URLFetchService}. 
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Sep 4, 2012
 */
public final class HTTPRequest {

    /**
     * URL.
     */
    private URL url;
    /**
     * Payload. 
     */
    private byte[] payload;
    /**
     * Payload map.
     */
    // XXX: payload abstraction
    private Map<String, String> payloadMap = new HashMap<String, String>();
    /**
     * Request method.
     */
    private HTTPRequestMethod requestMethod = HTTPRequestMethod.GET;
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
     * Gets payload map.
     * 
     * <p>
     * Certain HTTP methods ({@linkplain HTTPRequestMethod#GET GET}) will 
     * NOT have any payload, and this method will return an empty map.
     * </p>
     * 
     * @return payload map
     */
    public Map<String, String> getPayloadMap() {
        return Collections.unmodifiableMap(payloadMap);
    }

    /**
     * Adds the specified name and value to payload map.
     * 
     * <p>
     * This method should NOT be called for certain HTTP methods 
     * (e.g. {@link HTTPRequestMethod#GET GET}).
     * </p>
     * 
     * @param name the specified name
     * @param value the specified value
     */
    public void addPayloadEntry(final String name, final String value) {
        payloadMap.put(name, value);
    }

    /**
     * Gets the payload ({@link HTTPRequestMethod#POST POST} data body).
     * 
     * <p>
     * Certain HTTP methods ({@linkplain HTTPRequestMethod#GET GET}) will 
     * NOT have any payload, and this method will return {@code null}.
     * </p>
     * 
     * @return payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Sets the payload with the specified payload.
     * 
     * <p>
     * This method should NOT be called for certain HTTP methods 
     * (e.g. {@link HTTPRequestMethod#GET GET}).
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
}
