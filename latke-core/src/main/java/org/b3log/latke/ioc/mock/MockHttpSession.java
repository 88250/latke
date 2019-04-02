/*
 * Copyright (c) 2009-present, b3log.org
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
package org.b3log.latke.ioc.mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A mock HTTP session for test mainly.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, May 21, 2009
 */
public final class MockHttpSession implements HttpSession {

    /**
     * Id.
     */
    private static AtomicInteger id = new AtomicInteger(0);

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getId() {
        return Integer.toString(id.getAndIncrement());
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getValue(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void putValue(final String name, final Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeValue(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
