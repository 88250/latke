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
