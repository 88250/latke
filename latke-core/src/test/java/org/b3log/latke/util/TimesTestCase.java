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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * {@link Times} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Jun 21, 2019
 * @since 2.4.49
 */
public class TimesTestCase {

    @Test
    public void getDayStartTime() {
        final long now = System.currentTimeMillis();
        final long dayStartTime = Times.getDayStartTime(now);
        final String pattern = "yyyyMMdd HH:mm:ss";
        final String start = DateFormatUtils.format(dayStartTime, pattern);
        System.out.println("day start: " + start);
    }

    @Test
    public void getDayEndTime() {
        final long now = System.currentTimeMillis();
        final long dayEndTime = Times.getDayEndTime(now);
        final String pattern = "yyyyMMdd HH:mm:ss";
        final String end = DateFormatUtils.format(dayEndTime, pattern);
        System.out.println("day end: " + end);
    }

    @Test
    public void getMonthStartTime() {
        final long now = System.currentTimeMillis();
        final long monthStartTime = Times.getMonthStartTime(now);
        final String pattern = "yyyyMMdd HH:mm:ss";
        final String start = DateFormatUtils.format(monthStartTime, pattern);
        System.out.println("month start: " + start);

        final Date lastMonth = DateUtils.addMonths(new Date(), -1);
        final long lastMonthStartTime = Times.getMonthStartTime(lastMonth.getTime());
        final String lastMonthStart = DateFormatUtils.format(lastMonthStartTime, pattern);
        System.out.println("last month start: " + lastMonthStart);
    }

    @Test
    public void getMonthEndTime() {
        final long now = System.currentTimeMillis();
        final long monthEndTime = Times.getMonthEndTime(now);
        final String pattern = "yyyyMMdd HH:mm:ss";
        final String end = DateFormatUtils.format(monthEndTime, pattern);
        System.out.println("month end: " + end);

        final Date lastMonth = DateUtils.addMonths(new Date(), -1);
        final long lastMonthEndTime = Times.getMonthEndTime(lastMonth.getTime());
        final String lastMonthEnd = DateFormatUtils.format(lastMonthEndTime, pattern);
        System.out.println("last month end: " + lastMonthEnd);
    }

    @Test
    public void isSameDay() {
        final Date now1 = new Date();
        final Date now2 = new Date();

        boolean isSameDay = Times.isSameDay(now1, now2);
        Assert.assertTrue(isSameDay);

        final Date lastMonth = DateUtils.addMonths(now2, -1);
        isSameDay = Times.isSameDay(now2, lastMonth);
        Assert.assertFalse(isSameDay);
    }
}
