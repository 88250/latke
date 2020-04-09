/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * LianDi is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.service;


/**
 * Service exception.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
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
