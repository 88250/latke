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
package org.b3log.latke.http;

/**
 * Enumerations of HTTP request methods.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 4, 2018
 */
public enum HttpMethod {

    /**
     * Indicates HTTP GET method.
     */
    GET,
    /**
     * Indicates HTTP HEAD method.
     */
    HEAD,
    /**
     * Indicates HTTP POST method.
     */
    POST,
    /**
     * Indicates HTTP PUT method.
     */
    PUT,
    /**
     * Indicates HTTP DELETE method.
     */
    DELETE,
    /**
     * Indicates HTTP OPTIONS method.
     */
    OPTIONS,
    /**
     * Indicates HTTP TRACE method.
     */
    TRACE
}
