/*
 * Copyright (c) 2009-2019, b3log.org & hacpai.com
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

import javax.servlet.*;
import java.io.IOException;

/**
 * General encoding filter.
 *
 * <p>
 * Configured in web.xml filter. Init param requestEncoding & responseEncoding.
 * </p>
 *
 * @author ArmstrongCN
 * @version 1.0.0.0, Aug 10, 2012
 */
public final class EncodingFilter implements Filter {

    /**
     * Request encoding.
     */
    private String requestEncoding = "UTF-8";

    /**
     * Response encoding.
     */
    private String responseEncoding = "UTF-8";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        requestEncoding = filterConfig.getInitParameter("requestEncoding");
        responseEncoding = filterConfig.getInitParameter("responseEncoding");
    }

    /**
     * Sets the request and response encoding to a given charset.
     *
     * @param request  the specified request
     * @param response the specified response
     * @param chain    filter chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(requestEncoding);
        response.setCharacterEncoding(responseEncoding);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
