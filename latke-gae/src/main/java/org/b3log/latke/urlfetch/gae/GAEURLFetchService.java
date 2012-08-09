/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.urlfetch.gae;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPHeader;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;

/**
 * Google App Engine URL fetch service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 4, 2011
 */
public final class GAEURLFetchService implements URLFetchService {

    /**
     * URL fetch service.
     */
    private static final com.google.appengine.api.urlfetch.URLFetchService URL_FETCH_SERVICE =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Default fetch timeout, measures in seconds.
     */
    private static final double DEFAULT_TIMEOUT = 5D;

    @Override
    public HTTPResponse fetch(final HTTPRequest request) throws IOException {
        final com.google.appengine.api.urlfetch.HTTPRequest gaeHTTPRequest = toGAEHTTPRequest(request);
        final com.google.appengine.api.urlfetch.HTTPResponse gaeHTTPResponse = URL_FETCH_SERVICE.fetch(gaeHTTPRequest);

        return toHTTPResponse(gaeHTTPResponse);
    }

    /**
     * {@inheritDoc}
     * 
     * @return future <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/urlfetch/HTTPResponse.html">
     * GAE response</a>
     */
    @Override
    public Future<?> fetchAsync(final HTTPRequest request) {
        final com.google.appengine.api.urlfetch.HTTPRequest gaeHTTPRequest = toGAEHTTPRequest(request);

        return URL_FETCH_SERVICE.fetchAsync(gaeHTTPRequest);
    }

    /**
     * Converts the specified Google App Engine HTTP response to a HTTP response.
     * 
     * @param response the specified Google App Engine HTTP response
     * @return HTTP response
     */
    private static HTTPResponse toHTTPResponse(
            final com.google.appengine.api.urlfetch.HTTPResponse response) {
        final HTTPResponse ret = new HTTPResponse();

        ret.setContent(response.getContent());
        ret.setFinalURL(response.getFinalUrl());
        ret.setResponseCode(response.getResponseCode());

        final List<com.google.appengine.api.urlfetch.HTTPHeader> gaeHTTPHeaders = response.getHeaders();
        for (final com.google.appengine.api.urlfetch.HTTPHeader gaeHTTPHeader : gaeHTTPHeaders) {
            final HTTPHeader header = new HTTPHeader(gaeHTTPHeader.getName(), gaeHTTPHeader.getValue());
            ret.addHeader(header);
        }

        return ret;
    }

    /**
     * Converts the specified HTTP request to a Google App Engine HTTP request.
     * 
     * @param request the specified HTTP request
     * @return GAE HTTP request
     */
    private static com.google.appengine.api.urlfetch.HTTPRequest toGAEHTTPRequest(final HTTPRequest request) {
        final URL url = request.getURL();
        final HTTPRequestMethod requestMethod = request.getRequestMethod();

        com.google.appengine.api.urlfetch.HTTPRequest ret = null;

        switch (requestMethod) {
            case GET:
                ret = new com.google.appengine.api.urlfetch.HTTPRequest(url);
                break;
            case DELETE:
                ret = new com.google.appengine.api.urlfetch.HTTPRequest(url, HTTPMethod.DELETE);
                break;
            case HEAD:
                ret = new com.google.appengine.api.urlfetch.HTTPRequest(url, HTTPMethod.HEAD);
                break;
            case POST:
                ret = new com.google.appengine.api.urlfetch.HTTPRequest(url, HTTPMethod.POST);
                break;
            case PUT:
                ret = new com.google.appengine.api.urlfetch.HTTPRequest(url, HTTPMethod.PUT);
                break;
            default:
                throw new RuntimeException("Unsupported HTTP request method[" + requestMethod.name() + "]");
        }

        final List<HTTPHeader> headers = request.getHeaders();
        for (final HTTPHeader header : headers) {
            ret.addHeader(new com.google.appengine.api.urlfetch.HTTPHeader(header.getName(), header.getValue()));
        }

        ret.setPayload(request.getPayload());
        ret.getFetchOptions().setDeadline(DEFAULT_TIMEOUT);

        return ret;
    }
}
