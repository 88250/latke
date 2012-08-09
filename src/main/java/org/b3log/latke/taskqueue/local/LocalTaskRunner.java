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
package org.b3log.latke.taskqueue.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.Latkes;
import org.b3log.latke.taskqueue.Task;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;

/**
 * run the task in queue, now using httpUrlfetch to handle the request.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Apr 6, 2012
 */
public class LocalTaskRunner extends Thread {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalTaskRunner.class.getName());
    /**
     * the task need to do .
     */
    private Task task;
    /**
     * the retry time of a task.
     */
    private Integer retryLimit;

    /**
     * default constructor.
     * 
     * @param task task need to do 
     * @param retryLimit retryTime
     */
    public LocalTaskRunner(final Task task, final Integer retryLimit) {
        this.task = task;
        this.retryLimit = retryLimit;
    }
    /**
     * using urlFetchService to do the TASK.
     */
    private URLFetchService urlFetchService;

    @Override
    public void run() {

        urlFetchService = URLFetchServiceFactory.getURLFetchService();

        final HTTPRequest httpRequest = new HTTPRequest();
        try {
            httpRequest.setURL(new URL(Latkes.getServer() + Latkes.getContextPath() + task.getURL()));
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }

        Integer retry = 0;
        while (retry < retryLimit) {

            if (!doUrlFetch(httpRequest)) {
                retry++;
                try {
                    Thread.sleep(new Integer("1000"));
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }

        }

    }

    /**
     * do task using urlfetch(method:get),if wrong return false.
     * 
     * @param httpRequest {@link HTTPRequest}
     * @return isSuccess
     */
    private boolean doUrlFetch(final HTTPRequest httpRequest) {

        HTTPResponse httpResponse = null;
        try {
            httpResponse = urlFetchService.fetch(httpRequest);
        } catch (final IOException e) {
            LOGGER.log(Level.INFO, "The task[{0}] throw exception {1}", new Object[]{task.getURL(), e.getMessage()});
            return false;
        }

        /**
         * <p> Quote GAE:"
         * If a push task request handler returns an HTTP status code within the range 200–299, 
         * App Engine considers the task to have completed successfully. 
         * If the task returns a status code outside of this range" 
         *</p>
         */
        final Integer beginCode = 200;
        final Integer endCode = 299;

        if (httpResponse.getResponseCode() >= beginCode && httpResponse.getResponseCode() <= endCode) {
            return true;
        }
        LOGGER.log(Level.INFO, "The task[{0}] not success ,the return code is [{1}]",
                   new Object[]{task.getURL(), httpResponse.getResponseCode()});

        return false;
    }
}
