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

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Date utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jun 19, 2011
 */
public final class Dates {

    /**
     * English month.
     */
    public static final Map<String, String> EN_MONTHS = new HashMap<>();

    static {
        EN_MONTHS.put("01", "January");
        EN_MONTHS.put("02", "February");
        EN_MONTHS.put("03", "March");
        EN_MONTHS.put("04", "April");
        EN_MONTHS.put("05", "May");
        EN_MONTHS.put("06", "June");
        EN_MONTHS.put("07", "Jule");
        EN_MONTHS.put("08", "August");
        EN_MONTHS.put("09", "September");
        EN_MONTHS.put("10", "October");
        EN_MONTHS.put("11", "November");
        EN_MONTHS.put("12", "December");
    }

    /**
     * Private constructor.
     */
    private Dates() {
    }

    /**
     * Gets current date time string with the specified date formatter.
     *
     * @param dateFormat the specified date formatter
     * @return a date time string
     */
    public static String currentDatetime(final DateFormat dateFormat) {
        return dateFormat.format(System.currentTimeMillis());
    }
}
