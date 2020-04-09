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

import java.util.Arrays;

/**
 * Array utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jun 20, 2013
 */
public final class ArrayUtils {

    /**
     * Concatenates the specified arrays.
     *
     * @param <T>   the type of array element
     * @param first the specified first array
     * @param rest  the specified rest arrays
     * @return concatenated array
     */
    public static <T> T[] concatenate(final T[] first, final T[]... rest) {
        int totalLength = first.length;

        for (final T[] array : rest) {
            totalLength += array.length;
        }

        final T[] ret = Arrays.copyOf(first, totalLength);
        int offset = first.length;

        for (final T[] array : rest) {
            System.arraycopy(array, 0, ret, offset, array.length);
            offset += array.length;
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private ArrayUtils() {
    }
}
