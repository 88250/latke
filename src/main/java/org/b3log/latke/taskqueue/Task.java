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
package org.b3log.latke.taskqueue;

import java.io.Serializable;
import org.b3log.latke.servlet.HTTPRequestMethod;

/**
 * Task.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Feb 24, 2012
 */
public final class Task implements Serializable {
    
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * URL.
     */
    private String url;
    /**
     * Name.
     */
    private String name;
    /**
     * HTTP method.
     */
    private HTTPRequestMethod requestMethod = HTTPRequestMethod.GET;

    /**
     * Gets the request method.
     * 
     * @return request method of this task
     */
    public HTTPRequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets the request method with the specified request method.
     * 
     * @param requestMethod the specified request method
     */
    public void setRequestMethod(final HTTPRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Gets the URL of this task.
     * 
     * @return URI of this task
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the URL with the specified URL.
     * 
     * @param url the specified URL
     */
    public void setURL(final String url) {
        this.url = url;
    }

    /**
     * Gets the name of this task.
     * 
     * @return name of this task
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name with the specified name.
     * 
     * @param name the specified name
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder().
                append("url=").append(url).append(", name=").append(name).append(
                ", requestMethod=").append(requestMethod);

        return stringBuilder.toString();
    }
}
