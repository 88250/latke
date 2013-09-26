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

import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.config.Discoverer;
import org.b3log.latke.mock.MockHttpServletRequest;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.Ihandler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.PrepareHandler;
import org.b3log.latke.servlet.handler.RequestMatchHandler;
import org.b3log.latke.servlet.mock.TestBeforeAdvice;
import org.b3log.latke.servlet.mock.TestService;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import static org.mockito.Mockito.*;

/**
 * User: steveny Date: 13-9-25 Time: 下午1:19
 */
public class ProcessorTest {

	private final List<Ihandler> handlerList = new ArrayList<Ihandler>();

	@BeforeTest
	@SuppressWarnings("unchecked")
	public void beforeTest() throws Exception {
		System.out.println("Request Processors Test");
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(TestService.class);
		classes.add(TestBeforeAdvice.class);

		Lifecycle.startApplication(classes);

		handlerList.add(new RequestMatchHandler());
		handlerList.add(new PrepareHandler());
		handlerList.add(new AdviceHandler());
		handlerList.add(new MethodInvokeHandler());
	}

	@AfterTest
	public void afterTest() {
		System.out.println("afterTest SpeakerUnitTest");
		Lifecycle.endApplication();
	}

	@Test
	public void testBaseInvoke1() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/string");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));
		Assert.assertEquals("string", control.data(MethodInvokeHandler.INVOKE_RESULT));

	}

	@Test
	public void testBaseInvoke2() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/string/aa/bb");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
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
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
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
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
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
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
		Assert.assertEquals("a", args.get("name"));
		Assert.assertEquals("b", args.get("password"));
		Assert.assertEquals("ba", control.data(MethodInvokeHandler.INVOKE_RESULT));

	}

	@Test
	public void testBaseInvoke6() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/date/1/20120306");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
		Assert.assertEquals(1, args.get("id"));
		Assert.assertTrue(args.get("date") instanceof Date);
		Assert.assertEquals("11330963200000", control.data(MethodInvokeHandler.INVOKE_RESULT));

	}

	@Test
	public void testBaseInvoke7() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/dobefore/1");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
		Assert.assertEquals(2, args.get("id"));
		Assert.assertEquals(2, control.data(MethodInvokeHandler.INVOKE_RESULT));

	}

	@Test
	public void testBaseInvoke8() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/do/render");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		Assert.assertTrue(control.data(MethodInvokeHandler.INVOKE_RESULT) instanceof DoNothingRenderer);

	}

	@Test
	public void testBaseInvoke9() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/do/render1");
		when(request.getMethod()).thenReturn("GET");

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));

		List<AbstractHTTPResponseRenderer> list = (List<AbstractHTTPResponseRenderer>) control
				.data(MethodInvokeHandler.INVOKE_RESULT);

		final int totalMatched = 3;
		Assert.assertEquals(list.size(), totalMatched);
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
				put("id", new String[] { "1" });
				put("des", new String[] { "aa", "bbb" });
			}
		});

		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));
		
		Map<String, Object> args = (Map<String, Object>) control.data(PrepareHandler.PREPARE_ARGS);
		JSONObject jsonObject =(JSONObject) args.get("jsonObject");
		Assert.assertTrue(jsonObject.get("des") instanceof String[]);
		Assert.assertEquals("n", control.data(MethodInvokeHandler.INVOKE_RESULT));

	}

	public HttpControl doFlow(HttpServletRequest req) {
		HTTPRequestContext httpRequestContext = new HTTPRequestContext();
		httpRequestContext.setRequest(req);
		HttpControl httpControl = new HttpControl(handlerList.iterator(), httpRequestContext);
		httpControl.nextHandler();
		return httpControl;
	}

}
