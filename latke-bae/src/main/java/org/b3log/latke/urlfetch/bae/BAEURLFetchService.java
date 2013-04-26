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


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.b3log.latke.servlet.HTTPRequestMethod;
import static org.b3log.latke.servlet.HTTPRequestMethod.DELETE;
import static org.b3log.latke.servlet.HTTPRequestMethod.GET;
import static org.b3log.latke.servlet.HTTPRequestMethod.HEAD;
import static org.b3log.latke.servlet.HTTPRequestMethod.POST;
import static org.b3log.latke.servlet.HTTPRequestMethod.PUT;
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
        final HttpClient httpClient = new DefaultHttpClient();

        final URL url = request.getURL();
        final HTTPRequestMethod requestMethod = request.getRequestMethod();

        HttpUriRequest httpUriRequest = null;

        try {
            final byte[] payload = request.getPayload();

            switch (requestMethod) {

            case GET:
                final HttpGet httpGet = new HttpGet(url.toURI());

                // FIXME: GET with payload
                httpUriRequest = httpGet;
                break;

            case DELETE:
                httpUriRequest = new HttpDelete(url.toURI());
                break;

            case HEAD:
                httpUriRequest = new HttpHead(url.toURI());
                break;

            case POST:
                final HttpPost httpPost = new HttpPost(url.toURI());

                if (null != payload) {
                    httpPost.setEntity(new ByteArrayEntity(payload));
                }
                httpUriRequest = httpPost;
                break;

            case PUT:
                final HttpPut httpPut = new HttpPut(url.toURI());

                if (null != payload) {
                    httpPut.setEntity(new ByteArrayEntity(payload));
                }
                httpUriRequest = httpPut;
                break;

            default:
                throw new RuntimeException("Unsupported HTTP request method[" + requestMethod.name() + "]");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "URL fetch failed", e);

            throw new IOException("URL fetch failed [msg=" + e.getMessage() + ']');
        }

        final List<HTTPHeader> headers = request.getHeaders();

        for (final HTTPHeader header : headers) {
            httpUriRequest.addHeader(header.getName(), header.getValue());
        }

        final HttpResponse res = httpClient.execute(httpUriRequest);

        final HTTPResponse ret = new HTTPResponse();

        ret.setContent(EntityUtils.toByteArray(res.getEntity()));
        ret.setResponseCode(res.getStatusLine().getStatusCode());

        return ret;
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
}
