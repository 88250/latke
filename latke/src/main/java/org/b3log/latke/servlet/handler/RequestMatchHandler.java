package org.b3log.latke.servlet.handler;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午3:42
 */
public class RequestMatchHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestMatchHandler.class.getName());


    public RequestMatchHandler() {

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final Set<LatkeBean<?>> processBeans = beanManager.getBeans(RequestProcessor.class);
        genInfo(processBeans);
    }


    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {
        HttpServletRequest request = context.getRequest();

        String requestURI = getRequestURI(request);
        String method = getMethod(request);

        LOGGER.log(Level.DEBUG, "Request[requestURI={0}, method={1}]", new Object[]{requestURI, method});
    }

    private String getMethod(HttpServletRequest request) {
        String method = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);

        if (Strings.isEmptyOrNull(method)) {
            method = request.getMethod();
        }
        return method;
    }

    private String getRequestURI(HttpServletRequest request) {
        String requestURI = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);

        if (Strings.isEmptyOrNull(requestURI)) {
            requestURI = request.getRequestURI();
        }
        return requestURI;
    }

    private void genInfo(Set<LatkeBean<?>> processBeans) {

        for (final LatkeBean<?> latkeBean : processBeans) {
            final Class<?> clz = latkeBean.getBeanClass();

            final Method[] declaredMethods = clz.getDeclaredMethods();

            for (int i = 0; i < declaredMethods.length; i++) {
                final Method mthd = declaredMethods[i];
                final RequestProcessing requestProcessingMethodAnn = mthd.getAnnotation(RequestProcessing.class);

                if (null == requestProcessingMethodAnn) {
                    continue;
                }

                LOGGER.log(Level.DEBUG, "Added a processor method[className={0}], method[{1}]",
                        new Object[]{clz.getCanonicalName(), mthd.getName()});

                addProcessorInfo(requestProcessingMethodAnn, clz, mthd);
            }
        }
    }

    private void addProcessorInfo(RequestProcessing requestProcessingMethodAnn, Class<?> clz, Method mthd) {

    }

}

class ProcessorInfo {

}
