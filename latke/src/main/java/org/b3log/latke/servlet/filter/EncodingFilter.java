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
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * General EncodingFilter.
 * Configured in web.xml filter.
 * init param requestEncoding & responseEncoding
 * 
 * @author ArmstrongCN
 * @version 0.1, Aug 10, 2012
 */
public class EncodingFilter  implements Filter{
    
    private String requestEncoding="UTF-8";
    private String responseEncoding="UTF-8";
    
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(EncodingFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        requestEncoding=filterConfig.getInitParameter("requestEncoding");
        responseEncoding=filterConfig.getInitParameter("responseEncoding");
    }

    /**
     * Set the request and response encoding to a given charset. 
     *
     * @param request the specified request
     * @param response the specified response
     * @param chain filter chain
     * @throws IOException io exception
     * @throws ServletException servlet exception
     * 
     * @see web.xml filter
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
