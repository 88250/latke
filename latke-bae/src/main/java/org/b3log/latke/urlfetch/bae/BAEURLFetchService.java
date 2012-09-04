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
package org.b3log.latke.urlfetch.bae;

import com.baidu.bae.api.fetchurl.BaeFetchurl;
import com.baidu.bae.api.fetchurl.BaeFetchurlFactory;
import com.baidu.bae.api.fetchurl.BasicNameValuePair;
import com.baidu.bae.api.fetchurl.NameValuePair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPHeader;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;

/**
 * Baidu App Engine URL fetch service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 4, 2012
 */
public final class BAEURLFetchService implements URLFetchService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BAEURLFetchService.class.getName());

    @Override
    public HTTPResponse fetch(final HTTPRequest request) throws IOException {
        final BaeFetchurl baeFetchurl = BaeFetchurlFactory.getBaeFetchurl();

        addHeaders(baeFetchurl, request);

        if (HTTPRequestMethod.POST == request.getRequestMethod()) {
            final Map<String, String> payloadMap = request.getPayloadMap();

            if (!payloadMap.isEmpty()) {
                final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                for (final Map.Entry<String, String> payload : payloadMap.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(payload.getKey(), payload.getValue()));
                }

                baeFetchurl.setPostData(nameValuePairs);
            }
        }

        baeFetchurl.fetch(request.getURL().toString(), request.getRequestMethod().name());

        return toHTTPResponse(baeFetchurl);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * <b>Note</b>: Dose <em>NOT</em> support async URL fetch at present, calls this method is equivalent to call 
     * {@link #fetch(org.b3log.latke.urlfetch.HTTPRequest)}.
     * </p>
     */
    @Override
    public Future<?> fetchAsync(final HTTPRequest request) {
        final FutureTask<HTTPResponse> futureTask = new FutureTask<HTTPResponse>(new Callable<HTTPResponse>() {
            @Override
            public HTTPResponse call() throws Exception {
                return fetch(request);
            }
        });

        // no pool
        futureTask.run();

        return futureTask;
    }

    /**
     * Converts the specified BAE fetchurl to a HTTP response.
     * 
     * @param baeFetchurl the specified BAE fetchurl
     * @return HTTP response
     */
    private static HTTPResponse toHTTPResponse(final BaeFetchurl baeFetchurl) {
        final HTTPResponse ret = new HTTPResponse();

        ret.setContent(baeFetchurl.getResponseBody().getBytes());
        ret.setResponseCode(baeFetchurl.getHttpCode());

        final Map<String, String> responseHeader = baeFetchurl.getResponseHeader();
        for (final Map.Entry<String, String> header : responseHeader.entrySet()) {
            ret.addHeader(new HTTPHeader(header.getKey(), header.getValue()));
        }

        return ret;
    }

    /**
     * Adds the HTTP headers for the specified BAE Fetchurl from the specified request.
     * 
     * @param baeFetchurl the specified BAE Fetchurl
     * @param request the specified request
     */
    private static void addHeaders(final BaeFetchurl baeFetchurl, final HTTPRequest request) {
        final List<HTTPHeader> headers = request.getHeaders();
        for (final HTTPHeader header : headers) {
            baeFetchurl.setHeader(header.getName(), header.getValue());
        }
    }
}
