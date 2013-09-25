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
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.Ihandler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.PrepareAndExecuteHandler;
import org.b3log.latke.servlet.handler.RequestMatchHandler;
import org.b3log.latke.servlet.mock.TestService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

		Lifecycle.startApplication(classes);

		handlerList.add(new RequestMatchHandler());
		handlerList.add(new PrepareAndExecuteHandler());
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
		when(request.getRequestURI()).thenReturn("/string/a/b");
		when(request.getMethod()).thenReturn("GET");
		
		HttpControl control = doFlow(request);
		Assert.assertNotNull(control.data(RequestMatchHandler.MATCH_RESULT));
		Assert.assertEquals("ab", control.data(MethodInvokeHandler.INVOKE_RESULT));
		
	}
	
	
	

	public HttpControl doFlow(HttpServletRequest req) {
		HTTPRequestContext httpRequestContext = new HTTPRequestContext();
		httpRequestContext.setRequest(req);
		HttpControl httpControl = new HttpControl(handlerList.iterator(), httpRequestContext);
		httpControl.nextHandler();
		return httpControl;
	}

}
