/*
 * Copyright (c) 2009-present, b3log.org
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

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.servlet.handler.AfterHandleHandler;
import org.b3log.latke.servlet.handler.ContextHandleHandler;
import org.b3log.latke.servlet.handler.Handler;
import org.b3log.latke.servlet.handler.RouteHandler;
import org.b3log.latke.servlet.mock.*;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor test.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
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
        final List<Class<?>> classes = new ArrayList<>();
        classes.add(TestRequestProcessor.class);
        classes.add(TestBeforeAdvice.class);
        classes.add(TestAfterAdvice.class);
        BeanManager.start(classes);

        final TestRequestProcessor testRequestProcessor = BeanManager.getInstance().getReference(TestRequestProcessor.class);
        DispatcherServlet.get("/l", testRequestProcessor::l);
        DispatcherServlet.get("/lbefore", testRequestProcessor::lbefore);
        DispatcherServlet.mapping();

        handlerList.add(new RouteHandler());
        handlerList.add(new AfterHandleHandler());
        handlerList.add(new ContextHandleHandler());
    }

    @AfterTest
    public void afterTest() {
        BeanManager.close();
    }

    @Test
    public void a() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/a");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext context = DispatcherServlet.handle(request, response);
        Assert.assertEquals(context.attr("a"), "a");
    }

    @Test
    public void a1() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/a/88250/D");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext context = DispatcherServlet.handle(request, response);
        Assert.assertEquals(context.attr("id"), "88250");
        Assert.assertEquals(context.attr("name"), "D");
    }

    @Test
    public void abefore() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/a/before");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext context = DispatcherServlet.handle(request, response);
        Assert.assertEquals(context.attr("before"), "before");
        Assert.assertEquals(context.attr("abefore"), "abefore");
    }

    @Test
    public void l() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/l");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext context = DispatcherServlet.handle(request, response);
        Assert.assertEquals(context.attr("l"), "l");
    }

    @Test
    public void lbefore() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/lbefore");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext context = DispatcherServlet.handle(request, response);
        Assert.assertEquals(context.attr("before"), "before");
        Assert.assertEquals(context.attr("after"), "after");
        Assert.assertEquals(context.attr("lbefore"), "lbefore");
    }

}
