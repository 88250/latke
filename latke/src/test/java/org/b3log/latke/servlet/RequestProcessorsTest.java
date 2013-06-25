/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.servlet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.config.Discoverer;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.testhelper.VirtualObject;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * test for {@link RequestProcessors}.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jun 20, 2013
 */
public class RequestProcessorsTest {

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("Request Processors Test");
        final Collection<Class<?>> classes = Discoverer.discover("org.blog,org.b3log.latke");

        Lifecycle.startApplication(classes);

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();

        // Build processors
        final Set<LatkeBean<?>> processBeans = beanManager.getBeans(RequestProcessor.class);
        RequestProcessors.buildProcessorMethods(processBeans);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest SpeakerUnitTest");
        Lifecycle.endApplication();
    }

    /**
     * test {@link RequestProcessors}.invoke method(easy one).
     */
    @Test
    public void testInvoke() {
        final String requestURI = "/string";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "string");

    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern1() {
        final String requestURI = "/string/11/tom";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "11tom");
    }

    /**(AfterRequestProcessAdvice) adviceMap.get(clz)
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern11() {
        final String requestURI = "/string/11ptom";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "11tom");
    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern2() {
        final String requestURI = "/name--password";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "passwordname");
    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern3() {
        final String requestURI = "/date/1/20120306";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "11330963200000");
    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokeBefore() {
        final String requestURI = "/before/12";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals(ret, "12");
    }

    /**
     * test scanClass.
     * @throws Exception Exception  
     */
    @Test
    public void testScanClass() throws Exception {
        final VirtualObject requestProcessors = new VirtualObject("org.b3log.latke.servlet.RequestProcessors");
        final HashSet hashSet = (HashSet<?>) requestProcessors.getValue("processorMethods");
        final int totalMatched = 14;
        Assert.assertEquals(hashSet.size(), totalMatched);
    }

    /**
     * testInitRender.
     */
    @Test
    public void testInitRender() {
        Latkes.initRuntimeEnv();

        String requestURI = "/do/render";
        final AbstractHTTPResponseRenderer ret = (AbstractHTTPResponseRenderer) RequestProcessors.invoke(requestURI, "/", "GET",
                new HTTPRequestContext());
        Assert.assertNotNull(ret);

        requestURI = "/do/render1";
        final List<AbstractHTTPResponseRenderer> list = (List<AbstractHTTPResponseRenderer>) RequestProcessors.invoke(requestURI, "/",
                "GET", new HTTPRequestContext());
        final int totalMatched = 3;
        Assert.assertEquals(list.size(), totalMatched);
        Assert.assertTrue(list.get(0) instanceof JSONRenderer);
        Assert.assertTrue(list.get(1) instanceof DoNothingRenderer);
        Assert.assertTrue(list.get(2) instanceof JSONRenderer);
        Assert.assertFalse(list.get(0) == list.get(2));
    }
}
