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
package org.b3log.latke.urlfetch.local;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import org.b3log.latke.urlfetch.HTTPRequest;

/**
 * Specific Handler for urlfetch.
 * 
 * match {@link org.b3log.latke.servlet.HTTPRequestMethod}<br>POST</br>
 * Override {@link #configConnection(HttpURLConnection, HTTPRequest)}
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 0.0.0.2, Aug 15, 2011
 * 
 */
class UrlFetchPostHandler extends UrlFetchCommonHandler {

    @Override
    protected void configConnection(final HttpURLConnection httpURLConnection, final HTTPRequest request)
            throws IOException {
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);

        if (request.getPayload() != null) {

            final OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(request.getPayload());

            outputStream.flush();
            outputStream.close();
        }

    }
}
