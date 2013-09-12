package org.b3log.latke.servlet.handler;

import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;

/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午2:32
 */
public interface Ihandler {

    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception;

}
