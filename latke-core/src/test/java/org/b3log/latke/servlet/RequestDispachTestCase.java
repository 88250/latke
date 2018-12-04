/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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

import junit.framework.Assert;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.Handler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.RouteHandler;
import org.b3log.latke.servlet.mock.TestBeforeAdvice;
import org.b3log.latke.servlet.mock.TestRequestProcessor;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Processor test.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 2, 2018
 */
public class RequestDispachTestCase {

    private final List<Handler> handlerList = new ArrayList<>();

    static {
        Latkes.init();
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("Request Processors Test");
        final List<Class<?>> classes = new ArrayList<>();
        classes.add(TestRequestProcessor.class);
        classes.add(TestBeforeAdvice.class);
        BeanManager.start(classes);

        final TestRequestProcessor testRequestProcessor = BeanManager.getInstance().getReference(TestRequestProcessor.class);
        DispatcherServlet.get("/func1", testRequestProcessor::lambdaRoute);
        DispatcherServlet.get("/func2/{arg_name}", testRequestProcessor::lambdaRoute);
        DispatcherServlet.mapping();

        handlerList.add(new RouteHandler());
        handlerList.add(new AdviceHandler());
        handlerList.add(new MethodInvokeHandler());
    }

    @AfterTest
    public void afterTest() {
        System.out.println("afterTest SpeakerUnitTest");
        BeanManager.close();
    }

    @Test
    public void testFunctionalRouting() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/func1");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RouteHandler.MATCH_RESULT));
        Assert.assertNull(control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    @Test
    public void testFunctionalRouting1() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/func2/argVar");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RouteHandler.MATCH_RESULT));
        Assert.assertNull(control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    @Test
    public void testBaseInvoke1() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RouteHandler.MATCH_RESULT));
        Assert.assertEquals("string", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testRetVoid() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/void");
        when(request.getMethod()).thenReturn("GET");

        final HttpControl control = doFlow(request);

        Assert.assertNotNull(control.data(RouteHandler.MATCH_RESULT));
        Assert.assertNull(control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    public HttpControl doFlow(HttpServletRequest req) {
        RequestContext httpRequestContext = new RequestContext();
        httpRequestContext.setRequest(req);
        HttpControl ret = new HttpControl(handlerList.iterator(), httpRequestContext);
        ret.nextHandler();

        return ret;
    }
}
