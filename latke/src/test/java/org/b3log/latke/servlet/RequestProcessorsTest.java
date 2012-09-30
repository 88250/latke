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
package org.b3log.latke.servlet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.b3log.latke.servlet.converter.ConvertSupport;
import org.b3log.latke.testhelper.MockService;
import org.b3log.latke.testhelper.VirtualObject;
import org.testng.annotations.Test;

/**
 * test for {@link RequestProcessors}.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 3, 2012
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class RequestProcessorsTest extends TestCase {

    static {
        /**
        final VirtualObject service = new VirtualObject("org.b3log.latke.testhelper.MockService");
        final VirtualObject requestProcessors = new VirtualObject("org.b3log.latke.servlet.RequestProcessors");
        final HashSet hashSet = (HashSet<?>) requestProcessors.getValue("processorMethods");
        hashSet.add(registerServiceAndMethod(service, "/string", "getString", new Class<?>[]{}, ConvertSupport.class));
        hashSet.add(registerServiceAndMethod(service, "/string/{id}/{name}", "getString1", new Class<?>[]{Integer.class, String.class},
                ConvertSupport.class));
        hashSet.add(registerServiceAndMethod(service, "/string/{id}p{name}", "getString11", new Class<?>[]{Integer.class, String.class},
                ConvertSupport.class));
        hashSet.add(registerServiceAndMethod(service, "/{name}--{password}", "getString2", new Class<?>[]{String.class, String.class},
                ConvertSupport.class));
        hashSet.add(registerServiceAndMethod(service, "/date/{id}/{date}", "getString2", new Class<?>[]{Integer.class, Date.class},
                MockConverSupport.class));
         */
        RequestProcessors.discoverFromClass(MockService.class);
    }

    /**
     * registerServiceAndMethod for a dispath mapping for UT.
     *
     * @param service the ServiceHolder
     * @param uriPattern the uriPattern
     * @param methodName the methodName
     * @param clazz the class[] types of the method paramss
     * @param convertClazz the custom ConvertClazz
     * @return the processorMethod in {@link RequestProcessors}
     *
     */
    @Deprecated
    private static Object registerServiceAndMethod(final VirtualObject service, final String uriPattern, final String methodName,
            final Class<?>[] clazz, final Class<? extends ConvertSupport> convertClazz) {
        final VirtualObject processorMethod = new VirtualObject("org.b3log.latke.servlet.RequestProcessors$ProcessorMethod");
        processorMethod.setValue("uriPattern", uriPattern);
        processorMethod.setValue("withContextPath", false);
        processorMethod.setValue("uriPatternMode", URIPatternMode.ANT_PATH);
        processorMethod.setValue("method", HTTPRequestMethod.GET.name());
        processorMethod.setValue("processorClass", service.getInstanceClass());
        processorMethod.setValue("processorMethod", service.getInstanceMethod(methodName, clazz));
        processorMethod.setValue("convertClass", convertClazz);
        try {
            processorMethod.getInstanceMethod("analysis", new Class[] {}).invoke(processorMethod.getInstance(), null);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return processorMethod.getInstance();
    }

    /**
     * test {@link RequestProcessors}.invoke method(easy one).
     */
    @Test
    public void testInvoke() {

        final String requestURI = "/string";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("string", ret);

    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern1() {

        final String requestURI = "/string/11/tom";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("11tom", ret);

    }

    /**(AfterRequestProcessAdvice) adviceMap.get(clz)
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern11() {

        final String requestURI = "/string/11ptom";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("11tom", ret);

    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern2() {

        final String requestURI = "/name--password";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("passwordname", ret);

    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokePattern3() {

        final String requestURI = "/date/1/20120306";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("11330963200000", ret);

    }

    /**
     * test {@link RequestProcessors}.invoke method(using pattern).
     */
    @Test
    public void testInvokeBefore() {

        final String requestURI = "/before/12";
        final String ret = (String) RequestProcessors.invoke(requestURI, "/", "GET", new HTTPRequestContext());
        Assert.assertEquals("12", ret);

    }
}
