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
package org.b3log.latke.urlfetch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Future;

import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.testng.annotations.Test;

/**
 * URL fetch test case.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 0.0.0.2, Aug 21, 2011
 */
public class UrlFetchTestCase {

    static {
        Latkes.initRuntimeEnv();
    }
    /**
     * URL fetch service.
     */
    private final URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();

    /**
     * 
     * @throws IOException XXX
     */
    @Test
    public void testGetFetch() throws IOException {
        System.out.println("testGetFetch");
        final HTTPRequest request = new HTTPRequest();
        request.setRequestMethod(HTTPRequestMethod.GET);
        request.setURL(new URL("http://www.baidu.com"));

        final HTTPResponse httpResponse = fetchService.fetch(request);

        printHttpResponse(httpResponse);
    }

    /**
     * Tests async get fetch.
     * 
     * @throws Exception exception
     */
    @Test
    public void testAsyncGetFetch() throws Exception {
        System.out.println("testAsyncGetFetch");
        final HTTPRequest request = new HTTPRequest();
        request.setRequestMethod(HTTPRequestMethod.GET);
        request.setURL(new URL("http://www.baidu.com"));
        
        final Future<?> fetchAsync = fetchService.fetchAsync(request);
        final HTTPResponse httpResponse = (HTTPResponse) fetchAsync.get();

        printHttpResponse(httpResponse);
    }

    /**
     * 
     * @throws IOException  XXX
     */
    @Test
    public void testPostFetch() throws IOException {
        System.out.println("testPostFetch");
        final HTTPRequest request = new HTTPRequest();
        request.setRequestMethod(HTTPRequestMethod.POST);
        request.setURL(new URL("https://passport.baidu.com/api/?login"));

        final String content = URLEncoder.encode(
                "username=yaoliceng&password=09101112", "UTF-8");
        request.setPayload(content.getBytes());

        final HTTPResponse httpResponse = fetchService.fetch(request);

        printHttpResponse(httpResponse);
    }

    /**
     * 
     * @param httpResponse XXX
     * @throws IOException XXX
     */
    private void printHttpResponse(final HTTPResponse httpResponse) throws
            IOException {
        System.out.println("responseCode == " + httpResponse.getResponseCode());
        System.out.println("finalUrl == " + httpResponse.getFinalURL());

        for (HTTPHeader httpHeader : httpResponse.getHeaders()) {
            System.out.println(httpHeader.getName() + " == " + httpHeader.getValue());
        }

        final BufferedReader reader =
                             new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                httpResponse.getContent())));

        String lines;
        while ((lines = reader.readLine()) != null) {
            System.out.println(lines);
        }
    }
}
