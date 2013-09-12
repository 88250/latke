package org.b3log.latke.servlet;

import org.b3log.latke.servlet.handler.Ihandler;

import java.util.Iterator;

/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午2:36
 */
public class HttpControl {

    public HttpControl(Iterator<Ihandler> ihandlerIterable, HTTPRequestContext httpRequestContext) {

        this.ihandlerIterable = ihandlerIterable;
        this.httpRequestContext = httpRequestContext;
    }

    private Iterator<Ihandler> ihandlerIterable;

    private HTTPRequestContext httpRequestContext;

    public void nextHandler() {
        if (ihandlerIterable.hasNext()) {

            try {
                ihandlerIterable.next().handle(httpRequestContext, this);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
