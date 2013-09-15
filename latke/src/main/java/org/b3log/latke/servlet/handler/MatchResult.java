package org.b3log.latke.servlet.handler;

import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: mainlove
 * Date: 13-9-15
 * Time: 下午7:19
 */
public class MatchResult {

    private ProcessorInfo processorInfo;

    private String requestURI;

    private String matchedMethod;

    private String matchedPattern;

    private Method invokeMethond;

    private Map<String, Object> mapValues;

    private final List<AbstractHTTPResponseRenderer> rendererList = new ArrayList<AbstractHTTPResponseRenderer>();


    MatchResult(ProcessorInfo processorInfo, String requestURI, String matchedMethod, String matchedPattern) {
        this.processorInfo = processorInfo;
        this.requestURI = requestURI;
        this.matchedMethod = matchedMethod;
        this.matchedPattern = matchedPattern;
        this.invokeMethond = processorInfo.getInvokeHolder();
    }

    public MatchResult() {
    }

    public ProcessorInfo getProcessorInfo() {
        return processorInfo;
    }

    public void setProcessorInfo(ProcessorInfo processorInfo) {
        this.processorInfo = processorInfo;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getMatchedMethod() {
        return matchedMethod;
    }

    public void setMatchedMethod(String matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    public String getMatchedPattern() {
        return matchedPattern;
    }

    public void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }

    public Map<String, Object> getMapValues() {
        return mapValues;
    }

    public void setMapValues(Map<String, Object> mapValues) {
        this.mapValues = mapValues;
    }

    public Method getInvokeMethond() {
        return invokeMethond;
    }

    public void setInvokeMethond(Method invokeMethond) {
        this.invokeMethond = invokeMethond;
    }

    public void addRenders(AbstractHTTPResponseRenderer ins) {
        rendererList.add(ins);
    }

    public List<AbstractHTTPResponseRenderer> getRendererList() {
        return rendererList;
    }
}