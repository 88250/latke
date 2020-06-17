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

import org.json.JSONArray;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Collection utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.8, Oct 21, 2012
 */
public final class CollectionUtils {

    /**
     * Private constructor.
     */
    private CollectionUtils() {
    }

    /**
     * Gets a list of integers(size specified by the given size) between the
     * specified start(inclusion) and end(inclusion) randomly.
     *
     * @param start the specified start
     * @param end   the specified end
     * @param size  the given size
     * @return a list of integers
     */
    public static List<Integer> getRandomIntegers(final int start, final int end, final int size) {
        if (size > (end - start + 1)) {
            throw new IllegalArgumentException("The specified size more then (end - start + 1)!");
        }

        final List<Integer> integers = genIntegers(start, end);
        Collections.shuffle(integers);

        return integers.subList(0, size);
    }

    /**
     * Generates a list of integers from the specified start(inclusion) to the
     * specified end(inclusion).
     *
     * @param start the specified start
     * @param end   the specified end
     * @return a list of integers
     */
    public static List<Integer> genIntegers(final int start, final int end) {
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }

    /**
     * Converts the specified array to a set.
     *
     * @param <T>   the type of elements maintained by the specified array
     * @param array the specified array
     * @return a hash set
     */
    public static <T> Set<T> arrayToSet(final T[] array) {
        if (null == array) {
            return Collections.emptySet();
        }

        return new HashSet<>(Arrays.asList(array));
    }

    /**
     * Converts the specified {@link List list} to a
     * {@link JSONArray JSON array}.
     *
     * @param <T>  the type of elements maintained by the specified list
     * @param list the specified list
     * @return a {@link JSONArray JSON array}
     */
    public static <T> JSONArray listToJSONArray(final List<T> list) {
        return toJSONArray(list);
    }

    /**
     * Converts the specified {@link Collection collection} to a {@link JSONArray JSON array}.
     *
     * @param <T>        the type of elements maintained by the specified collection
     * @param collection the specified collection
     * @return a {@link JSONArray JSON array}
     */
    public static <T> JSONArray toJSONArray(final Collection<T> collection) {
        return new JSONArray(collection);
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to a
     * {@link List list}.
     *
     * @param <T>       the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @return an {@link ArrayList array list}
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> jsonArrayToSet(final JSONArray jsonArray) {
        if (null == jsonArray) {
            return Collections.emptySet();
        }

        final Set<T> ret = new HashSet<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add((T) jsonArray.opt(i));
        }

        return ret;
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to a
     * {@link List list}.
     *
     * @param <T>       the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @return an {@link ArrayList array list}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonArrayToList(final JSONArray jsonArray) {
        return (List<T>) Optional.ofNullable(jsonArray)
                .map(JSONArray::toList).orElse(Collections.emptyList());
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to an array.
     *
     * @param <T>       the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @param newType   the class of the copy to be returned
     * @return an array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] jsonArrayToArray(final JSONArray jsonArray, final Class<? extends T[]> newType) {
        return (T[]) Optional.ofNullable(jsonArray)
                .map(JSONArray::toList).map(List::toArray).orElse(new Object[]{});
    }
}
