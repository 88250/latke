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
package org.b3log.latke.urlfetch.local;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.urlfetch.HTTPHeader;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * Common handler for URL fetch.
 * <p>
 * match {@link org.b3log.latke.servlet.HTTPRequestMethod}<br>GET, HEAD</br>
 * the core method is {@link #doFetch(HTTPRequest)}
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.6, May 12, 2017
 */
class UrlFetchCommonHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UrlFetchCommonHandler.class);

    static {
        try {
            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Init SSL context failed", e);
        }
    }

    /**
     * doFetch- the template method.
     *
     * @param request the specified request
     * @return {@link HTTPResponse}
     * @throws IOException IOException from java.net
     * @see #prepareConnection(HTTPRequest)
     * @see #configConnection(HttpURLConnection, HTTPRequest)
     * @see #resultConnection(HttpURLConnection)
     */
    protected HTTPResponse doFetch(final HTTPRequest request) throws IOException {
        final HttpURLConnection httpURLConnection = prepareConnection(request);

        configConnection(httpURLConnection, request);
        httpURLConnection.connect();

        final HTTPResponse ret = resultConnection(httpURLConnection);
        httpURLConnection.disconnect();

        return ret;
    }

    /**
     * @param request the specified HTTP request
     * @return {@link HttpURLConnection}
     * @throws IOException IOException from java.net
     */
    protected HttpURLConnection prepareConnection(final HTTPRequest request) throws IOException {
        if (request.getURL() == null) {
            throw new IOException("URL for URLFetch should not be null");
        }

        final HttpURLConnection ret = (HttpURLConnection) request.getURL().openConnection();
        ret.setRequestMethod(request.getRequestMethod().toString());
        for (final HTTPHeader httpHeader : request.getHeaders()) {
            ret.setRequestProperty(httpHeader.getName(), httpHeader.getValue());
        }

        // Properties prop = System.getProperties();
        // prop.setProperty("http.proxyHost", "10.1.2.188");
        // prop.setProperty("http.proxyPort", "80");
        // prop.setProperty("https.proxyHost", "10.1.2.188");
        // prop.setProperty("https.proxyPort", "80");
        return ret;
    }

    /**
     * @param httpURLConnection {@link HttpURLConnection}
     * @param request           the specified HTTP request
     * @throws IOException IOException from java.net
     */
    protected void configConnection(final HttpURLConnection httpURLConnection, final HTTPRequest request)
            throws IOException {
        httpURLConnection.setConnectTimeout(request.getConnectTimeout());
        httpURLConnection.setReadTimeout(request.getReadTimeout());
    }

    /**
     * @param httpURLConnection {@link HttpURLConnection}
     * @return HTTPResponse the http response
     * @throws IOException IOException from java.net
     */
    protected HTTPResponse resultConnection(final HttpURLConnection httpURLConnection) throws IOException {
        final HTTPResponse ret = new HTTPResponse();

        ret.setResponseCode(httpURLConnection.getResponseCode());
        ret.setFinalURL(httpURLConnection.getURL());

        InputStream retStream;
        if (HttpServletResponse.SC_OK <= ret.getResponseCode() && ret.getResponseCode() < HttpServletResponse.SC_BAD_REQUEST) {
            retStream = httpURLConnection.getInputStream();
        } else {
            retStream = httpURLConnection.getErrorStream();
        }
        ret.setContent(inputStreamToByte(retStream));

        fillHttpResponseHeader(ret, httpURLConnection.getHeaderFields());

        return ret;
    }

    /**
     * @param httpResponse HTTP Rsponse
     * @param headerFields headerFiedls in HTTP response
     */
    protected void fillHttpResponseHeader(final HTTPResponse httpResponse, final Map<String, List<String>> headerFields) {
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            httpResponse.addHeader(new HTTPHeader(entry.getKey(), entry.getValue().toString()));
        }
    }

    /**
     * @param is {@link InputStream}
     * @return Byte[]
     * @throws IOException from java.io
     */
    // XXX need to move to 'util'
    private byte[] inputStreamToByte(final InputStream is) throws IOException {
        final ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }

        final byte[] ret = bytestream.toByteArray();
        bytestream.close();

        return ret;
    }

    /**
     * Default trust manager.
     */
    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
