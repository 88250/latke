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

import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Local URL fetch service.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.5, Jun 14, 2017
 */
public final class LocalURLFetchService implements URLFetchService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalURLFetchService.class);

    @Override
    public HTTPResponse fetch(final HTTPRequest request) throws IOException {
        final HTTPRequestMethod requestMethod = request.getRequestMethod();

        if (requestMethod == null) {
            throw new IOException("RequestMethod  for URLFetch should not be null");
        }

        return UrlFetchHandlerFactory.getFetchHandler(requestMethod).doFetch(request);
    }

    @Override
    public Future<?> fetchAsync(final HTTPRequest request) {
        final FutureTask<HTTPResponse> ret = new FetchTask(() -> {
            LOGGER.log(Level.DEBUG, "Fetch async, request=[" + request.toString() + "]");

            return fetch(request);
        }, request);

        Latkes.EXECUTOR_SERVICE.submit(ret);

        return ret;
    }

    /**
     * URL fetch task.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Jan 23, 2017
     */
    private static class FetchTask extends FutureTask<HTTPResponse> {

        /**
         * Request.
         */
        private HTTPRequest request;

        /**
         * Constructs a fetch task with the specified callable and request.
         *
         * @param callable the specified callable
         * @param request  the specified request
         */
        FetchTask(final Callable<HTTPResponse> callable, final HTTPRequest request) {
            super(callable);

            this.request = request;
        }

        @Override
        public String toString() {
            return "URL Fetch [request=" + request.toString() + "]";
        }
    }
}
