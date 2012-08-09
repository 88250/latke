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
package org.b3log.latke.servlet.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Abstract HTTP response GZIP filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 16, 2010
 */
public abstract class AbstractGZIPFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractGZIPFilter.class.getName());

    @Override
    public void init(final FilterConfig cfg) throws ServletException {
    }

    /**
     * Wraps the http servlet response with GZIP if could.
     *
     * @param request the specified request
     * @param response the specified response
     * @param chain filter chain
     * @throws IOException io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String requestURI = httpServletRequest.getRequestURI();
        if (shouldSkip(requestURI)) {
            LOGGER.log(Level.FINEST, "Skip GZIP filter request[URI={0}]", requestURI);
            chain.doFilter(request, response);

            return;
        }

        final String acceptEncoding = httpServletRequest.getHeader("Accept-Encoding");
        boolean supportGZIP = false;
        if (null != acceptEncoding
            && 0 <= acceptEncoding.indexOf("gzip")) {
            supportGZIP = true;
        }

        if (!supportGZIP) {
            LOGGER.info("Gzip NOT be supported");
            chain.doFilter(request, response);

            return;
        }

        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.addHeader("Content-Encoding", "gzip");
        httpServletResponse.addHeader("Vary", "Accept-Encoding");
        chain.doFilter(request, new GZIPServletResponseWrapper(httpServletResponse));
    }

    /**
     * Determines whether the specified request URI should be skipped filter.
     *
     * <p>
     *   <b>Note</b>: This method SHOULD be invoked for all filters with pattern
     *   "/*".
     * </p>
     *
     * @param requestURI the specified request URI
     * @return {@code true} if should be skipped, {@code false} otherwise
     */
    public abstract boolean shouldSkip(final String requestURI);

    @Override
    public void destroy() {
    }

    /**
     * HTTP response wrapper for GZIP.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Dec 16, 2010
     */
    private class GZIPServletResponseWrapper extends HttpServletResponseWrapper {

        /**
         * GZIP output stream.
         */
        private GZIPOutputStream gzipStream;
        /**
         * Servlet output stream.
         */
        private ServletOutputStream servletOutputStream;
        /**
         * Print writer.
         */
        private PrintWriter printWriter;

        /**
         * Constructs an {@link GZIPServletResponseWrapper} object with the
         * specified http servlet response.
         *
         * @param httpServletResponse the specified http servlet response
         * @throws IOException io exception
         */
        GZIPServletResponseWrapper(final HttpServletResponse httpServletResponse)
                throws IOException {
            super(httpServletResponse);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (null == servletOutputStream) {
                servletOutputStream = createOutputStream();
            }

            return servletOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (null == printWriter) {
                printWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
            }

            return printWriter;
        }

        /**
         * Creates output stream with GZIP delegation.
         *
         * @return servlet output stream
         * @throws IOException io exception
         */
        private ServletOutputStream createOutputStream() throws IOException {
            final ServletResponse servletResponse = this.getResponse();
            gzipStream = new GZIPOutputStream(servletResponse.getOutputStream());

            return new ServletOutputStream() {

                @Override
                public void write(final int b) throws IOException {
                    gzipStream.write(b);
                }

                @Override
                public void flush() throws IOException {
                    gzipStream.flush();
                }

                @Override
                public void close() throws IOException {
                    gzipStream.close();
                }

                /*
                 * These two are not absolutely needed. They are here simply
                 * because they were overriden by GZIPOutputStream.
                 */
                @Override
                public void write(final byte[] b) throws IOException {
                    gzipStream.write(b);
                }

                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    gzipStream.write(b, off, len);
                }
            };
        }
    }
}
