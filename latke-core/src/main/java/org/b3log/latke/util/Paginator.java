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
package org.b3log.latke.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Paginator utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, May 22, 2018
 */
public final class Paginator {

    /**
     * Private constructor.
     */
    private Paginator() {
    }

    /**
     * Gets the current page number from the query string "p" of the specified request.
     *
     * @param request the specified request
     * @return page number, returns {@code 1} as default
     */
    public static int getPage(final HttpServletRequest request) {
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
    public static List<Integer> paginate(final int currentPageNum,
                                         final int pageSize,
                                         final int pageCount,
                                         final int windowSize) {
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
}
