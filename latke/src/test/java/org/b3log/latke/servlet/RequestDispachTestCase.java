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
import org.b3log.latke.servlet.handler.*;
import org.b3log.latke.servlet.mock.TestBeforeAdvice;
import org.b3log.latke.servlet.mock.TestRequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Processor test.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Dec 1, 2018
 */
public class RequestDispachTestCase {

    private final List<Handler> handlerList = new ArrayList<>();

    static {
        Latkes.init();
    }

    public void func2(final HTTPRequestContext context) {
        System.out.println("func2");
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("Request Processors Test");
        final List<Class<?>> classes = new ArrayList<>();
        classes.add(TestRequestProcessor.class);
        classes.add(TestBeforeAdvice.class);

        DispatcherServlet.route().get().uri("/func1").handler(c -> System.out.println("func1"));
        DispatcherServlet.route().get().uri("/func2").handler(this::func2);
        DispatcherServlet.mapping();

        BeanManager.start(classes);

        handlerList.add(new RequestDispatchHandler());
        handlerList.add(new ArgsHandler());
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
        when(request.getRequestURI()).thenReturn("/func");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));
        Assert.assertEquals("string", control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    @Test
    public void testBaseInvoke1() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));
        Assert.assertEquals("string", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke2() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string/aa/bb");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals("aa", args.get("id"));
        Assert.assertEquals("bb", args.get("name"));
        Assert.assertEquals("aabb", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke3() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string/aapbb");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals("aa", args.get("id"));
        Assert.assertEquals("bb", args.get("name"));
        Assert.assertEquals("aabb", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke4() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string/3*4");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals(3, args.get("a"));
        Assert.assertEquals(4, args.get("b"));
        Assert.assertEquals(12, control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke5() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/string/a+b");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals("a", args.get("name"));
        Assert.assertEquals("b", args.get("password"));
        Assert.assertEquals("ba", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke6() throws Exception {
        final String id = "1";

        final Date date = new Date();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String dateStr = dateFormat.format(date);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/date/" + id + "/" + dateStr);
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals(1, args.get("id"));
        Assert.assertTrue(args.get("date") instanceof Date);

        final Date date2 = dateFormat.parse(dateStr);

        Assert.assertEquals(id + date2.getTime(), control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    @Test
    public void testBaseInvoke7() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/dobefore/1");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        Assert.assertEquals(2, args.get("id"));
        Assert.assertEquals(2, control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testBaseInvoke8() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/do/render");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Assert.assertTrue(control.data(MethodInvokeHandler.INVOKE_RESULT) instanceof DoNothingRenderer);

    }

    @Test
    public void testBaseInvoke9() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/do/render1");
        when(request.getMethod()).thenReturn("GET");

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        List<AbstractHTTPResponseRenderer> list = (List<AbstractHTTPResponseRenderer>) control
                .data(MethodInvokeHandler.INVOKE_RESULT);

        final int totalMatched = 3;
        Assert.assertEquals(totalMatched, list.size());
        Assert.assertTrue(list.get(0) instanceof JSONRenderer);
        Assert.assertTrue(list.get(1) instanceof DoNothingRenderer);
        Assert.assertTrue(list.get(2) instanceof JSONRenderer);
        Assert.assertFalse(list.get(0) == list.get(2));

    }

    @Test
    public void testBaseInvoke10() throws JSONException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/json/n");
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>() {
            {
                put("id", new String[]{"1"});
                put("des", new String[]{"aa", "bbb"});
            }
        });

        HttpControl control = doFlow(request);
        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));

        Map<String, Object> args = (Map<String, Object>) control.data(ArgsHandler.PREPARE_ARGS);
        JSONObject jsonObject = (JSONObject) args.get("jsonObject");
        Assert.assertTrue(jsonObject.get("des") instanceof String[]);
        Assert.assertEquals("n", control.data(MethodInvokeHandler.INVOKE_RESULT));

    }

    @Test
    public void testRetVoid() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/void");
        when(request.getMethod()).thenReturn("GET");

        final HttpControl control = doFlow(request);

        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));
        Assert.assertNull(control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    @Test
    public void testAntPathMatch() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/a.html");
        when(request.getMethod()).thenReturn("GET");

        final HttpControl control = doFlow(request);

        Assert.assertNotNull(control.data(RequestDispatchHandler.MATCH_RESULT));
        Assert.assertNull(control.data(MethodInvokeHandler.INVOKE_RESULT));
    }

    public HttpControl doFlow(HttpServletRequest req) {
        HTTPRequestContext httpRequestContext = new HTTPRequestContext();
        httpRequestContext.setRequest(req);
        HttpControl ret = new HttpControl(handlerList.iterator(), httpRequestContext);
        ret.nextHandler();

        return ret;
    }
}
