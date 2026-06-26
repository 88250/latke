/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.util;

import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Paginator utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.0, Jun 26, 2026
 */
public final class Paginator {

    /**
     * 最大页码上限护栏。防止恶意/爬虫传入超大页码导致分页偏移量 (page-1)*size 整数溢出，
     * 进而引发负 LIMIT SQL 语法错误或参数越界。十万页远超任何真实分页场景。
     */
    private static final int MAX_PAGE_NUM = 100000;

    /**
     * Gets the current page number from the query string "p" of the specified context.
     *
     * @param context the specified context
     * @return page number, returns {@code 1} as default
     */
    public static int getPage(final RequestContext context) {
        return getPage(context.getRequest());
    }

    /**
     * Gets the current page number from the query string "p" of the specified request.
     *
     * @param request the specified request
     * @return page number, returns {@code 1} as default
     */
    public static int getPage(final Request request) {
        int ret = 1;
        final String p = request.getParameter("p");
        if (Strings.isNumeric(p)) {
            try {
                ret = Integer.parseInt(p);
            } catch (final Exception e) {
                // ignored
            }
        }

        if (1 > ret) {
            ret = 1;
        }

        if (ret > MAX_PAGE_NUM) {
            ret = MAX_PAGE_NUM;
        }

        return ret;
    }

    /**
     * Paginates with the specified current page number, page size, page count and window size.
     *
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @param pageCount      the specified page count
     * @param windowSize     the specified window size
     * @return a list integer pagination page numbers
     */
    public static List<Integer> paginate(final int currentPageNum, final int pageSize, final int pageCount, final int windowSize) {
        List<Integer> ret;
        if (pageCount < windowSize) {
            ret = new ArrayList<>(pageCount);
            for (int i = 0; i < pageCount; i++) {
                ret.add(i, i + 1);
            }
        } else {
            ret = new ArrayList<>(windowSize);
            int first = currentPageNum + 1 - windowSize / 2;

            first = first < 1 ? 1 : first;
            first = first + windowSize > pageCount ? pageCount - windowSize + 1 : first;
            for (int i = 0; i < windowSize; i++) {
                ret.add(i, first + i);
            }
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private Paginator() {
    }
}
