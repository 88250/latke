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


import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.servlet.advice.RequestReturnAdviceException;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.annotation.Render;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.converter.ConvertSupport;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.AntPathMatcher;
import org.b3log.latke.util.ReflectHelper;
import org.b3log.latke.util.RegexPathMatcher;
import org.json.JSONObject;


/**
 * Request processor utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.2.0.1, Jul 2, 2013
 */
public final class RequestProcessors {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestProcessors.class.getName());

    /**
     * Processor methods.
     */
    private static Set<ProcessorMethod> processorMethods = new HashSet<ProcessorMethod>();

    /**
     * Processors.
     */
    private static Map<Method, Object> processors = new HashMap<Method, Object>();

    /**
     * the renderer class Holder(do not  instantiate it witch has the different values of the variables>.
     */
    private static Map<String, Class<? extends AbstractHTTPResponseRenderer>> rendererMap = new HashMap<String, Class<? extends AbstractHTTPResponseRenderer>>();

    /**
     * the data convertMap cache.
     */
    private static Map<Class<? extends ConvertSupport>, ConvertSupport> convertMap = new HashMap<Class<? extends ConvertSupport>, ConvertSupport>();

    /**
     * Invokes a processor method with the specified request URI, method and context.
     *
     * @param requestURI the specified request URI
     * @param contextPath the specified context path
     * @param method the specified method
     * @param context the specified context
     * @return invoke result, returns {@code null} if invoke failed
     */
    public static Object invoke(final String requestURI, final String contextPath, final String method, final HTTPRequestContext context) {
        final ProcessorMethod processMethod = getProcessorMethod(requestURI, contextPath, method);

        if (null == processMethod) {
            LOGGER.log(Level.WARN, "Can not find process method for request[requestURI={0}, method={1}]", new Object[] {requestURI, method});
            return null;
        }

        final Method processorMethod = processMethod.getProcessorMethod();
        final Class<?> processorClass = processMethod.getProcessorClass();
        Object processorObject = processors.get(processorMethod);

        try {
            if (null == processorObject) {
                final Object instance = Lifecycle.getBeanManager().getReference(processorClass);

                processors.put(processorMethod, instance);
                processorObject = instance;
            }

            final Map<String, Object> args = new LinkedHashMap<String, Object>();

            final Class<?>[] parameterTypes = processorMethod.getParameterTypes();
            final String[] parameterName = processMethod.getMethodParamNames();

            // TODO need Optimization
            String relativeRequestURI = requestURI;

            if (contextPath != null && contextPath.length() > 1) {
                relativeRequestURI = requestURI.substring(contextPath.length());
            }
            final Map<String, String> pathVariableValueMap = processMethod.pathVariableValueMap(relativeRequestURI);

            final List<AbstractHTTPResponseRenderer> rendererList = new ArrayList<AbstractHTTPResponseRenderer>();

            // TODO: IoC managed

            for (int i = 0; i < parameterTypes.length; i++) {
                final Class<?> paramClass = parameterTypes[i];

                if (paramClass.equals(HTTPRequestContext.class)) {
                    args.put(parameterName[i], context);
                } else if (paramClass.equals(HttpServletRequest.class)) {
                    args.put(parameterName[i], context.getRequest());
                } else if (paramClass.equals(HttpServletResponse.class)) {
                    args.put(parameterName[i], context.getResponse());
                } else if (AbstractHTTPResponseRenderer.class.isAssignableFrom(paramClass)
                    && !paramClass.equals(AbstractHTTPResponseRenderer.class)) {
                    final AbstractHTTPResponseRenderer ins = (AbstractHTTPResponseRenderer) paramClass.newInstance();
                    final String rid = getRendererId(processorClass, processorMethod, i);

                    ins.setRendererId(rid);
                    rendererList.add(ins);
                    args.put(parameterName[i], ins);

                } else if (pathVariableValueMap.containsKey(parameterName[i])) {
                    args.put(parameterName[i],
                        getConverter(processMethod.getConvertClass()).convert(parameterName[i], pathVariableValueMap.get(parameterName[i]),
                        paramClass));
                } else {
                    args.put(parameterName[i], null);
                }
            }

            // before invoke(first class before advice and then method before advice).
            final List<Class<? extends BeforeRequestProcessAdvice>> beforeAdviceClassList = new ArrayList<Class<? extends BeforeRequestProcessAdvice>>();

            if (processorClass.isAnnotationPresent(Before.class)) {
                final Class<? extends BeforeRequestProcessAdvice>[] ac = processorClass.getAnnotation(Before.class).adviceClass();

                beforeAdviceClassList.addAll(Arrays.asList(ac));
            }
            if (processorMethod.isAnnotationPresent(Before.class)) {
                final Class<? extends BeforeRequestProcessAdvice>[] ac = processorMethod.getAnnotation(Before.class).adviceClass();

                beforeAdviceClassList.addAll(Arrays.asList(ac));
            }

            final LatkeBeanManager beanManager = Lifecycle.getBeanManager();

            BeforeRequestProcessAdvice binstance;

            try {
                for (Class<? extends BeforeRequestProcessAdvice> clz : beforeAdviceClassList) {
                    binstance = beanManager.getReference(clz);

                    binstance.doAdvice(context, args);
                }
            } catch (final RequestReturnAdviceException re) {
                return null;
            } catch (final RequestProcessAdviceException e) {
                final JSONObject exception = e.getJsonObject();

                LOGGER.log(Level.WARN, "Occurs an exception before request processing [errMsg={0}]", exception.optString(Keys.MSG));

                final JSONRenderer ret = new JSONRenderer();

                ret.setJSONObject(exception);
                context.setRenderer(ret);
                return null;
            }

            for (int j = 0; j < rendererList.size(); j++) {
                rendererList.get(j).preRender(context, args);
            }

            final Object ret = processorMethod.invoke(processorObject, args.values().toArray());

            for (int j = rendererList.size() - 1; j >= 0; j--) {
                rendererList.get(j).postRender(context, ret);
            }

            // after invoke(first method before advice and then class before advice).
            final List<Class<? extends AfterRequestProcessAdvice>> afterAdviceClassList = new ArrayList<Class<? extends AfterRequestProcessAdvice>>();

            if (processorMethod.isAnnotationPresent(After.class)) {
                final Class<? extends AfterRequestProcessAdvice>[] ac = processorMethod.getAnnotation(After.class).adviceClass();

                afterAdviceClassList.addAll(Arrays.asList(ac));
            }

            if (processorClass.isAnnotationPresent(After.class)) {
                final Class<? extends AfterRequestProcessAdvice>[] ac = processorClass.getAnnotation(After.class).adviceClass();

                afterAdviceClassList.addAll(Arrays.asList(ac));
            }

            AfterRequestProcessAdvice instance;

            for (Class<? extends AfterRequestProcessAdvice> clz : afterAdviceClassList) {
                instance = beanManager.getReference(clz);
                
                instance.doAdvice(context, ret);
            }

            return ret;

        } catch (final Exception e) {
            LOGGER.log(Level.ERROR,
                "Invokes processor method failed [method=" + processorMethod.getDeclaringClass().getSimpleName() + '#'
                + processorMethod.getName() + ']',
                e);

            return null;
        }
    }

    /**
     * getRendererId from mark {@link Render},using"-" as split:class_method_PARAMETER.
     * @param processorClass class
     * @param processorMethod method
     * @param i the index of the 
     * @return string
     */
    private static String getRendererId(final Class<?> processorClass, final Method processorMethod, final int i) {
        final StringBuilder sb = new StringBuilder();

        if (processorClass.isAnnotationPresent(Render.class)) {
            final String v = processorClass.getAnnotation(Render.class).value();

            if (StringUtils.isNotBlank(v)) {
                sb.append(v).append(v);
            }
        }

        if (processorMethod.isAnnotationPresent(Render.class)) {

            final String v = processorClass.getAnnotation(Render.class).value();

            if (StringUtils.isNotBlank(v)) {
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(v).append(v);
            }
        }

        for (java.lang.annotation.Annotation annotation : processorMethod.getParameterAnnotations()[i]) {
            if (annotation instanceof Render) {
                final String v = ((PathVariable) annotation).value();

                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(v).append(v);
            }
        }

        return sb.toString();
    }

    /**
     * get the converter in this method,using cache.
     * @param convertClass the class of {@link ConvertSupport}
     * @throws Exception Exception 
     * @return {@link ConvertSupport}
     */
    private static ConvertSupport getConverter(final Class<? extends ConvertSupport> convertClass) throws Exception {
        ConvertSupport ret = convertMap.get(convertClass);

        if (ret == null) {
            ret = convertClass.newInstance();
            convertMap.put(convertClass, ret);
        }

        return ret;
    }

    /**
     * Builds processor methods for the specified processor beans.
     * 
     * @param processorBeans the specified processor beans
     * @throws IOException io exception
     */
    public static void buildProcessorMethods(final Set<LatkeBean<?>> processorBeans) throws IOException {
        for (final LatkeBean<?> latkeBean : processorBeans) {
            final Class<?> clz = latkeBean.getBeanClass();

            final Method[] declaredMethods = clz.getDeclaredMethods();

            for (int i = 0; i < declaredMethods.length; i++) {
                final Method mthd = declaredMethods[i];
                final RequestProcessing requestProcessingMethodAnn = mthd.getAnnotation(RequestProcessing.class);

                if (null == requestProcessingMethodAnn) {
                    continue;
                }

                LOGGER.log(Level.DEBUG, "Added a processor method[className={0}], method[{1}]",
                    new Object[] {clz.getCanonicalName(), mthd.getName()});

                addProcessorMethod(requestProcessingMethodAnn, clz, mthd);
            }
        }

    }

    /**
     * Discover {@link RequestProcessor} to the ReuqestMapping from a specific class.
     * 
     * <p>
     *   <b>NOTE</b>: This method ONLY for test.
     * </p>
     * 
     * @param clazz the specific clazz need to be add Request Mapping
     */
    public static void discoverFromClass(final Class<?> clazz) {
        final RequestProcessor requestProcessor = clazz.getAnnotation(RequestProcessor.class);

        if (null == requestProcessor) {
            return;
        }

        final Method[] declaredMethods = clazz.getDeclaredMethods();

        for (int i = 0; i < declaredMethods.length; i++) {
            final Method mthd = declaredMethods[i];
            final RequestProcessing requestProcessingMethodAnn = mthd.getAnnotation(RequestProcessing.class);

            if (null == requestProcessingMethodAnn) {
                continue;
            }

            addProcessorMethod(requestProcessingMethodAnn, clazz, mthd);
        }
    }

    /**
     * Gets process method for the specified request URI and method.
     *
     * @param requestURI the specified request URI
     * @param contextPath the specified context path
     * @param method the specified method
     * @return process method, returns {@code null} if not found
     */
    private static ProcessorMethod getProcessorMethod(final String requestURI, final String contextPath, final String method) {
        LOGGER.log(Level.TRACE, "Gets processor method[requestURI={0}, contextPath={1}, method={2}]",
            new Object[] {requestURI, contextPath, method});

        final List<ProcessorMethod> matches = new ArrayList<ProcessorMethod>();
        int i = 0;

        for (final ProcessorMethod processorMethod : processorMethods) {
            // TODO: 88250, sort, binary-search
            if (method.equals(processorMethod.getMethod())) {
                // String uriPattern = processorMethod.getURIPattern();
                String uriPattern = processorMethod.getMappingString();

                if (processorMethod.isWithContextPath()) {
                    uriPattern = contextPath + uriPattern;
                }

                if (requestURI.equals(uriPattern)) {
                    return processorMethod;
                }

                boolean found = false;

                switch (processorMethod.getURIPatternMode()) {

                case ANT_PATH:
                    found = AntPathMatcher.match(uriPattern, requestURI);
                    break;

                case REGEX:
                    found = RegexPathMatcher.match(uriPattern, requestURI);
                    break;

                default:
                    throw new IllegalStateException(
                        "Can not process URI pattern[uriPattern=" + processorMethod.getURIPattern() + ", mode="
                        + processorMethod.getURIPatternMode() + "]");
                }

                if (found) {
                    i++;
                    matches.add(processorMethod);
                }
            }
        }

        if (matches.isEmpty()) {
            return null;
        }

        if (i > 1) {
            final StringBuilder stringBuilder = new StringBuilder("Can not determine request method for configured methods[");
            final Iterator<ProcessorMethod> iterator = matches.iterator();

            while (iterator.hasNext()) {
                final ProcessorMethod processMethod = iterator.next();

                stringBuilder.append("[className=");
                stringBuilder.append(processMethod.getProcessorMethod().getDeclaringClass().getSimpleName());
                stringBuilder.append(", methodName=");
                stringBuilder.append(processMethod.getProcessorMethod().getName());
                stringBuilder.append(", patterns=");
                stringBuilder.append(processMethod.getURIPattern());
                stringBuilder.append("]");

                if (iterator.hasNext()) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");

            LOGGER.warn(stringBuilder.toString());
        }

        return matches.get(0);
    }

    /**
     * Adds processor method by the specified annotation, class and method.
     *
     * @param requestProcessing the specified annotation
     * @param clz the specified class
     * @param method the specified method
     */
    private static void addProcessorMethod(final RequestProcessing requestProcessing, final Class<?> clz, final Method method) {
        final String[] uriPatterns = requestProcessing.value();
        final URIPatternMode uriPatternsMode = requestProcessing.uriPatternsMode();
        final boolean isWithContextPath = requestProcessing.isWithContextPath();

        for (int i = 0; i < uriPatterns.length; i++) {
            final String uriPattern = uriPatterns[i];
            final HTTPRequestMethod[] requestMethods = requestProcessing.method();

            for (int j = 0; j < requestMethods.length; j++) {
                final HTTPRequestMethod requestMethod = requestMethods[j];

                final ProcessorMethod processorMethod = new ProcessorMethod();

                processorMethod.setMethod(requestMethod.name());
                processorMethod.setURIPattern(uriPattern);
                processorMethod.setWithContextPath(isWithContextPath);
                processorMethod.setProcessorClass(clz);
                processorMethod.setProcessorMethod(method);
                processorMethod.setURIPatternModel(uriPatternsMode);
                processorMethod.setConvertClass(requestProcessing.convertClass());

                // processorMethod.setRenderer(initRenderer(method, clz));

                processorMethod.analysis();
                processorMethods.add(processorMethod);
            }
        }
    }

    /**
     * Default private constructor.
     */
    private RequestProcessors() {}

    /**
     * Request processor method.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.2, May 1, 2012
     */
    private static final class ProcessorMethod {

        /**
         * URI path pattern.
         */
        private String uriPattern;

        /**
         * Checks dose whether the URI pattern with the context path.
         */
        private boolean withContextPath;

        /**
         * URI pattern mode.
         */
        private URIPatternMode uriPatternMode;

        /**
         * Request method.
         */
        private String method;

        /**
         * Class.
         */
        private Class<?> processorClass;

        /**
         * Method.
         */
        private Method processorMethod;

        /**
         * the userdefined data converclass.
         */
        private Class<? extends ConvertSupport> convertClass;

        /**
         * the Renderer-class mapping holder.
         */
        private Map<String, Class<? extends AbstractHTTPResponseRenderer>> initRendererMap;

        /**
         * Sets the URI pattern mode with the specified URI pattern mode.
         *
         * @param uriPatternMode the specified URI pattern mode
         */
        public void setURIPatternModel(final URIPatternMode uriPatternMode) {
            this.uriPatternMode = uriPatternMode;
        }

        /**
         * Gets the URI pattern mode.
         *
         * @return URI pattern mode
         */
        public URIPatternMode getURIPatternMode() {
            return uriPatternMode;
        }

        /**
         * Gets method.
         *
         * @return method
         */
        public String getMethod() {
            return method;
        }

        /**
         * Sets the method with the specified method.
         *
         * @param method the specified method
         */
        public void setMethod(final String method) {
            this.method = method;
        }

        /**
         * Gets the processor class.
         *
         * @return processor class
         */
        public Class<?> getProcessorClass() {
            return processorClass;
        }

        /**
         * Sets the processor class with the specified processor class.
         *
         * @param processorClass the specified processor class
         */
        public void setProcessorClass(final Class<?> processorClass) {
            this.processorClass = processorClass;
        }

        /**
         * Gets the processor method.
         *
         * @return processor method
         */
        public Method getProcessorMethod() {
            return processorMethod;
        }

        /**
         * Sets the processor method with the specified processor method.
         *
         * @param processorMethod the specified processor method
         */
        public void setProcessorMethod(final Method processorMethod) {
            this.processorMethod = processorMethod;
        }

        /**
         * Gets the URI pattern.
         *
         * @return URI pattern
         */
        public String getURIPattern() {
            return uriPattern;
        }

        /**
         * Sets the URI pattern with the specified URI pattern.
         *
         * @param uriPattern the specified URI pattern
         */
        public void setURIPattern(final String uriPattern) {
            this.uriPattern = uriPattern;
        }

        /**
         * Checks dose whether the URI pattern with the context path.
         *
         * @return {@code true} if it is with the context path, returns {@code false} otherwise
         */
        public boolean isWithContextPath() {
            return withContextPath;
        }

        /**
         * Sets the with context path flag with the specified with context path flag.
         *
         * @param withContextPath the specified with context path flag
         */
        public void setWithContextPath(final boolean withContextPath) {
            this.withContextPath = withContextPath;
        }

        /**
         * @return the convertClass
         */
        public Class<? extends ConvertSupport> getConvertClass() {
            return convertClass;
        }

        /**
         * @param convertClass the convertClass to set
         */
        public void setConvertClass(final Class<? extends ConvertSupport> convertClass) {
            this.convertClass = convertClass;
        }

        /**
         * the mappingString for mapping.
         */
        private String mappingString;

        /**
         * @return the mappingString
         */
        public String getMappingString() {
            return mappingString;
        }

        /**
         * analysis the Pattern,do the other things to fill the pattren mapping.
         */
        public void analysis() {
            mappingString = handleMappingString();

            methodParamNames = ReflectHelper.getMethodVariableNames(processorClass, processorMethod.getName(),
                processorMethod.getParameterTypes());
            int i = 0;

            for (java.lang.annotation.Annotation[] annotations : processorMethod.getParameterAnnotations()) {
                for (java.lang.annotation.Annotation annotation : annotations) {
                    if (annotation instanceof PathVariable) {
                        methodParamNames[i] = ((PathVariable) annotation).value();
                    }
                }
                i++;
            }
        }

        /**
         * the paramNames in pattern.
         */
        private List<String> paramNames = new ArrayList<String>();

        /**
         * the posSpan in pattern.
         */
        private List<Integer> posSpan = new ArrayList<Integer>();

        /**
         * the character after the pattern.
         */
        private List<Character> afertCharacters = new ArrayList<Character>();

        /**
         * the Names in method.
         */
        private String[] methodParamNames;

        /**
         * @return the methodParamNames
         */
        public String[] getMethodParamNames() {
            return methodParamNames;
        }

        /**
         * using regex to get the mappingString,if no matching return the orgin uriPattern.
         *
         * @return the mappingString.
         */
        private String handleMappingString() {
            final Pattern pattern = Pattern.compile("\\{[^}]+\\}");
            final Matcher matcher = pattern.matcher(uriPattern);
            final StringBuilder uriMapping = new StringBuilder(uriPattern);
            int fixPos = 0;
            char[] tem;
            int lastEnd = 0;

            while (matcher.find()) {
                tem = new char[matcher.end() - matcher.start() - 2];
                uriMapping.getChars(matcher.start() - fixPos + 1, matcher.end() - fixPos - 1, tem, 0);
                paramNames.add(new String(tem));
                if (lastEnd == 0) {
                    posSpan.add(matcher.start());
                } else {
                    posSpan.add(matcher.start() - lastEnd);
                }

                uriMapping.replace(matcher.start() - fixPos, matcher.end() - fixPos, "*");
                fixPos = fixPos + matcher.end() - matcher.start() - 1;
                lastEnd = matcher.end();

                if (matcher.end() - fixPos < uriMapping.length()) {
                    afertCharacters.add(uriMapping.charAt(matcher.end() - fixPos));
                } else {
                    afertCharacters.add(null);
                }
            }

            return uriMapping.toString();
        }

        /**
         * get pathVariableValueMap in requestURI.
         *
         * @param requestURI requestURI
         * @return map
         * @throws Exception exception
         */
        public Map<String, String> pathVariableValueMap(final String requestURI) throws Exception {

            final Map<String, String> ret = new HashMap<String, String>();

            final int length = requestURI.length();
            int i = 0;
            StringBuilder chars;

            for (int j = 0; j < paramNames.size(); j++) {
                int step = 0;

                while (step < posSpan.get(j)) {
                    i++;
                    step++;
                }
                chars = new StringBuilder();
                while (i < length && Character.valueOf(requestURI.charAt(i)) != afertCharacters.get(j)) {
                    chars.append(requestURI.charAt(i));
                    i++;
                }

                ret.put(paramNames.get(j), URLDecoder.decode(chars.toString(), "UTF-8"));
            }

            return ret;
        }

        @Override
        public String toString() {
            return "ProcessorMethod [processorClass=" + processorClass.getName() + ", processorMethod=" + processorMethod.getName() + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int ret = 1;

            ret = prime * ret + (this.uriPattern != null ? this.uriPattern.hashCode() : 0);
            ret = prime * ret + (this.withContextPath ? 1 : 0);
            ret = prime * ret + (this.uriPatternMode != null ? this.uriPatternMode.hashCode() : 0);
            ret = prime * ret + (this.method != null ? this.method.hashCode() : 0);
            ret = prime * ret + (this.processorClass != null ? this.processorClass.hashCode() : 0);
            ret = prime * ret + (this.processorMethod != null ? this.processorMethod.hashCode() : 0);
            ret = prime * ret + (this.convertClass != null ? this.convertClass.hashCode() : 0);

            return ret;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ProcessorMethod other = (ProcessorMethod) obj;

            if ((this.uriPattern == null) ? (other.uriPattern != null) : !this.uriPattern.equals(other.uriPattern)) {
                return false;
            }
            if (this.withContextPath != other.withContextPath) {
                return false;
            }
            if (this.uriPatternMode != other.uriPatternMode) {
                return false;
            }
            if ((this.method == null) ? (other.method != null) : !this.method.equals(other.method)) {
                return false;
            }
            if (this.processorClass != other.processorClass
                && (this.processorClass == null || !this.processorClass.equals(other.processorClass))) {
                return false;
            }
            if (this.processorMethod != other.processorMethod
                && (this.processorMethod == null || !this.processorMethod.equals(other.processorMethod))) {
                return false;
            }
            if (this.convertClass != other.convertClass && (this.convertClass == null || !this.convertClass.equals(other.convertClass))) {
                return false;
            }
            return true;
        }
    }
}
