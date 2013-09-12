package org.b3log.latke.servlet;

import org.b3log.latke.servlet.handler.Ihandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private Map<String, Object> controlContext = new HashMap<String, Object>();

    public void data(String key, String value) {
        controlContext.put(key, value);
    }

    public Object data(String key) {
        return controlContext.get(key);
    }

    public void nextHandler() {
        if (ihandlerIterable.hasNext()) {
            try {
                ihandlerIterable.next().handle(httpRequestContext, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
