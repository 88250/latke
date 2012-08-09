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
package org.b3log.latke.service;

/**
 * Service exception.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 25, 2011
 */
public final class ServiceException extends Exception {
    
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Public default constructor.
     */
    public ServiceException() {
        super("Service exception!");
    }

    /**
     * Public constructor with {@link Throwable}.
     *
     * @param throwable the specified throwable object
     */
    public ServiceException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Public constructor with message.
     *
     * @param msg the specified message
     */
    public ServiceException(final String msg) {
        super(msg);
    }
}
