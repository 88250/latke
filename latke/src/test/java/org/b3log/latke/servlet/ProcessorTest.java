package org.b3log.latke.servlet;

import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.config.Discoverer;
import org.b3log.latke.mock.MockHttpServletRequest;
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.Ihandler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.PrepareAndExecuteHandler;
import org.b3log.latke.servlet.handler.RequestMatchHandler;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * User: steveny
 * Date: 13-9-25
 * Time: 下午1:19
 */
public class ProcessorTest {

    private final List<Ihandler> handlerList = new ArrayList<Ihandler>();


    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("Request Processors Test");
        final Collection<Class<?>> classes = Discoverer.discover("org.b3log.latke.servlet.mock");
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
    public void testInvoke() {


        String requestURI = "/string";
    }

    public HttpControl doFlow(HttpServletRequest req) {
        HTTPRequestContext httpRequestContext = new HTTPRequestContext();
        httpRequestContext.setRequest(req);
        HttpControl httpControl = new HttpControl(handlerList.iterator(), httpRequestContext);
        httpControl.nextHandler();
        return httpControl;
    }


}
