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
package org.b3log.latke.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;

/**
 * Collection utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jan 6, 2012
 */
public final class CollectionUtils {

    /**
     * Private default constructor.
     */
    private CollectionUtils() {
    }

    /**
     * Gets a list of integers(size specified by the given size) between the
     * specified start(inclusion) and end(inclusion) randomly.
     *
     * @param start the specified start
     * @param end the specified end
     * @param size the given size
     * @return a list of integers
     */
    public static List<Integer> getRandomIntegers(final int start, final int end, final int size) {
        if (size > (end - start + 1)) {
            throw new IllegalArgumentException("The specified size more then (end - start + 1)!");
        }

        final List<Integer> integers = genIntegers(start, end);
        final List<Integer> ret = new ArrayList<Integer>();

        int remainsSize;
        int index;
        while (ret.size() < size) {
            remainsSize = integers.size();
            index = (int) (Math.random() * (remainsSize - 1));
            final Integer i = integers.get(index);
            ret.add(i);
            integers.remove(i);
        }

        return ret;
    }

    /**
     * Generates a list of integers from the specified start(inclusion) to the
     * specified end(inclusion).
     *
     * @param start the specified start
     * @param end the specified end
     * @return a list of integers
     */
    public static List<Integer> genIntegers(final int start, final int end) {
        final List<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i <= end; i++) {
            ret.add(i + start);
        }

        return ret;
    }

    /**
     * Converts the specified array to a set.
     *
     * @param <T> the type of elements maintained by the specified array
     * @param array the specified array
     * @return a hash set
     */
    public static <T> Set<T> arrayToSet(final T[] array) {
        if (null == array) {
            return Collections.emptySet();
        }

        final Set<T> ret = new HashSet<T>();
        for (int i = 0; i < array.length; i++) {
            final T object = array[i];
            ret.add(object);
        }

        return ret;
    }

    /**
     * Converts the specified {@link List list} to a
     * {@link JSONArray JSON array}.
     *
     * @param <T> the type of elements maintained by the specified list
     * @param list the specified list
     * @return a {@link JSONArray JSON array}
     */
    public static <T> JSONArray listToJSONArray(final List<T> list) {
        final JSONArray ret = new JSONArray();

        if (null == list) {
            return ret;
        }

        for (final T object : list) {
            ret.put(object);
        }

        return ret;
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to a
     * {@link List list}.
     *
     * @param <T> the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @return an {@link ArrayList array list}
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> jsonArrayToSet(final JSONArray jsonArray) {
        if (null == jsonArray) {
            return Collections.emptySet();
        }

        final Set<T> ret = new HashSet<T>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add((T) jsonArray.opt(i));
        }

        return ret;
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to a
     * {@link List list}.
     *
     * @param <T> the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @return an {@link ArrayList array list}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonArrayToList(final JSONArray jsonArray) {
        if (null == jsonArray) {
            return Collections.emptyList();
        }

        final List<T> ret = new ArrayList<T>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add((T) jsonArray.opt(i));
        }

        return ret;
    }

    /**
     * Converts the specified {@link JSONArray JSON array} to an array.
     *
     * @param <T> the type of elements maintained by the specified json array
     * @param jsonArray the specified json array
     * @param newType the class of the copy to be returned
     * @return an array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] jsonArrayToArray(final JSONArray jsonArray, final Class<? extends T[]> newType) {
        if (null == jsonArray) {
            return (T[]) new Object[]{};
        }

        final int newLength = jsonArray.length();
        final Object[] original = new Object[newLength];
        for (int i = 0; i < newLength; i++) {
            original[i] = jsonArray.opt(i);
        }

        return Arrays.copyOf(original, newLength, newType);
    }
}
