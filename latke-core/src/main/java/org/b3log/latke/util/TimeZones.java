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

import java.util.*;

/**
 * https://github.com/nfergu/Java-Time-Zone-List.
 *
 * @author <a href="https://github.com/nfergu">Neil Ferguson</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Aug 1, 2018
 * @since 2.4.4
 */
public class TimeZones {

    private static final List<TimeZoneMapping> ZONE_MAPPINGS = new ArrayList<>();

    static {
        ZONE_MAPPINGS.add(new TimeZoneMapping("Afghanistan Standard Time", "Asia/Kabul", "(GMT +04:30) Kabul"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Alaskan Standard Time", "America/Anchorage", "(GMT -09:00) Alaska"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Arab Standard Time", "Asia/Riyadh", "(GMT +03:00) Kuwait, Riyadh"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Arabian Standard Time", "Asia/Dubai", "(GMT +04:00) Abu Dhabi, Muscat"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Arabic Standard Time", "Asia/Baghdad", "(GMT +03:00) Baghdad"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Argentina Standard Time", "America/Buenos_Aires", "(GMT -03:00) Buenos Aires"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Atlantic Standard Time", "America/Halifax", "(GMT -04:00) Atlantic Time (Canada)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("AUS Central Standard Time", "Australia/Darwin", "(GMT +09:30) Darwin"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("AUS Eastern Standard Time", "Australia/Sydney", "(GMT +10:00) Canberra, Melbourne, Sydney"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Azerbaijan Standard Time", "Asia/Baku", "(GMT +04:00) Baku"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Azores Standard Time", "Atlantic/Azores", "(GMT -01:00) Azores"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Bangladesh Standard Time", "Asia/Dhaka", "(GMT +06:00) Dhaka"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Canada Central Standard Time", "America/Regina", "(GMT -06:00) Saskatchewan"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Cape Verde Standard Time", "Atlantic/Cape_Verde", "(GMT -01:00) Cape Verde Is."));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Caucasus Standard Time", "Asia/Yerevan", "(GMT +04:00) Yerevan"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Cen. Australia Standard Time", "Australia/Adelaide", "(GMT +09:30) Adelaide"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central America Standard Time", "America/Guatemala", "(GMT -06:00) Central America"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Asia Standard Time", "Asia/Almaty", "(GMT +06:00) Astana"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Brazilian Standard Time", "America/Cuiaba", "(GMT -04:00) Cuiaba"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Europe Standard Time", "Europe/Budapest", "(GMT +01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central European Standard Time", "Europe/Warsaw", "(GMT +01:00) Sarajevo, Skopje, Warsaw, Zagreb"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Pacific Standard Time", "Pacific/Guadalcanal", "(GMT +11:00) Solomon Is., New Caledonia"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Standard Time (Mexico)", "America/Mexico_City", "(GMT -06:00) Guadalajara, Mexico City, Monterrey"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Central Standard Time", "America/Chicago", "(GMT -06:00) Central Time (US & Canada)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("China Standard Time", "Asia/Shanghai", "(GMT +08:00) Beijing, Chongqing, Hong Kong, Urumqi"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Dateline Standard Time", "Etc/GMT+12", "(GMT -12:00) International Date Line West"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("E. Africa Standard Time", "Africa/Nairobi", "(GMT +03:00) Nairobi"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("E. Australia Standard Time", "Australia/Brisbane", "(GMT +10:00) Brisbane"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("E. Europe Standard Time", "Europe/Minsk", "(GMT +02:00) Minsk"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("E. South America Standard Time", "America/Sao_Paulo", "(GMT -03:00) Brasilia"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Eastern Standard Time", "America/New_York", "(GMT -05:00) Eastern Time (US & Canada)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Egypt Standard Time", "Africa/Cairo", "(GMT +02:00) Cairo"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Yekaterinburg Standard Time", "Asia/Yekaterinburg", "(GMT +05:00) Yekaterinburg"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Fiji Standard Time", "Pacific/Fiji", "(GMT +12:00) Fiji, Marshall Is."));
        ZONE_MAPPINGS.add(new TimeZoneMapping("FLE Standard Time", "Europe/Kiev", "(GMT +02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Georgian Standard Time", "Asia/Tbilisi", "(GMT +04:00) Tbilisi"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GMT Standard Time", "Europe/London", "(GMT) Dublin, Edinburgh, Lisbon, London"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Greenland Standard Time", "America/Godthab", "(GMT -03:00) Greenland"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Greenwich Standard Time", "Atlantic/Reykjavik", "(GMT) Monrovia, Reykjavik"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GTB Standard Time", "Europe/Istanbul", "(GMT +02:00) Athens, Bucharest, Istanbul"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Hawaiian Standard Time", "Pacific/Honolulu", "(GMT -10:00) Hawaii"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("India Standard Time", "Asia/Calcutta", "(GMT +05:30) Chennai, Kolkata, Mumbai, New Delhi"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Iran Standard Time", "Asia/Tehran", "(GMT +03:30) Tehran"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Israel Standard Time", "Asia/Jerusalem", "(GMT +02:00) Jerusalem"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Jordan Standard Time", "Asia/Amman", "(GMT +02:00) Amman"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Kamchatka Standard Time", "Asia/Kamchatka", "(GMT +12:00) Petropavlovsk-Kamchatsky - Old"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Korea Standard Time", "Asia/Seoul", "(GMT +09:00) Seoul"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Magadan Standard Time", "Asia/Magadan", "(GMT +11:00) Magadan"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Mauritius Standard Time", "Indian/Mauritius", "(GMT +04:00) Port Louis"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Mid-Atlantic Standard Time", "Etc/GMT+2", "(GMT -02:00) Mid-Atlantic"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Middle East Standard Time", "Asia/Beirut", "(GMT +02:00) Beirut"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Montevideo Standard Time", "America/Montevideo", "(GMT -03:00) Montevideo"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Morocco Standard Time", "Africa/Casablanca", "(GMT) Casablanca"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Mountain Standard Time (Mexico)", "America/Chihuahua", "(GMT -07:00) Chihuahua, La Paz, Mazatlan"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Mountain Standard Time", "America/Denver", "(GMT -07:00) Mountain Time (US & Canada)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Myanmar Standard Time", "Asia/Rangoon", "(GMT +06:30) Yangon (Rangoon)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("N. Central Asia Standard Time", "Asia/Novosibirsk", "(GMT +06:00) Novosibirsk"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Namibia Standard Time", "Africa/Windhoek", "(GMT +02:00) Windhoek"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Nepal Standard Time", "Asia/Katmandu", "(GMT +05:45) Kathmandu"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("New Zealand Standard Time", "Pacific/Auckland", "(GMT +12:00) Auckland, Wellington"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Newfoundland Standard Time", "America/St_Johns", "(GMT -03:30) Newfoundland"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("North Asia East Standard Time", "Asia/Irkutsk", "(GMT +08:00) Irkutsk"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("North Asia Standard Time", "Asia/Krasnoyarsk", "(GMT +07:00) Krasnoyarsk"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Pacific SA Standard Time", "America/Santiago", "(GMT -04:00) Santiago"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Pacific Standard Time (Mexico)", "America/Tijuana", "(GMT -08:00) Baja California"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Pacific Standard Time", "America/Los_Angeles", "(GMT -08:00) Pacific Time (US & Canada)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Pakistan Standard Time", "Asia/Karachi", "(GMT +05:00) Islamabad, Karachi"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Paraguay Standard Time", "America/Asuncion", "(GMT -04:00) Asuncion"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Romance Standard Time", "Europe/Paris", "(GMT +01:00) Brussels, Copenhagen, Madrid, Paris"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Russian Standard Time", "Europe/Moscow", "(GMT +03:00) Moscow, St. Petersburg, Volgograd"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("SA Eastern Standard Time", "America/Cayenne", "(GMT -03:00) Cayenne, Fortaleza"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("SA Pacific Standard Time", "America/Bogota", "(GMT -05:00) Bogota, Lima, Quito"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("SA Western Standard Time", "America/La_Paz", "(GMT -04:00) Georgetown, La Paz, Manaus, San Juan"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Samoa Standard Time", "Pacific/Samoa", "(GMT -11:00) Samoa"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("SE Asia Standard Time", "Asia/Bangkok", "(GMT +07:00) Bangkok, Hanoi, Jakarta"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Singapore Standard Time", "Asia/Singapore", "(GMT +08:00) Kuala Lumpur, Singapore"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("South Africa Standard Time", "Africa/Johannesburg", "(GMT +02:00) Harare, Pretoria"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Sri Lanka Standard Time", "Asia/Colombo", "(GMT +05:30) Sri Jayawardenepura"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Syria Standard Time", "Asia/Damascus", "(GMT +02:00) Damascus"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Taipei Standard Time", "Asia/Taipei", "(GMT +08:00) Taipei"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Tasmania Standard Time", "Australia/Hobart", "(GMT +10:00) Hobart"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Tokyo Standard Time", "Asia/Tokyo", "(GMT +09:00) Osaka, Sapporo, Tokyo"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Tonga Standard Time", "Pacific/Tongatapu", "(GMT +13:00) Nuku'alofa"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Ulaanbaatar Standard Time", "Asia/Ulaanbaatar", "(GMT +08:00) Ulaanbaatar"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("US Eastern Standard Time", "America/Indianapolis", "(GMT -05:00) Indiana (East)"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("US Mountain Standard Time", "America/Phoenix", "(GMT -07:00) Arizona"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GMT ", "Etc/GMT", "(GMT) Coordinated Universal Time"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GMT +12", "Etc/GMT-12", "(GMT +12:00) Coordinated Universal Time+12"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GMT -02", "Etc/GMT+2", "(GMT -02:00) Coordinated Universal Time-02"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("GMT -11", "Etc/GMT+11", "(GMT -11:00) Coordinated Universal Time-11"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Venezuela Standard Time", "America/Caracas", "(GMT -04:30) Caracas"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Vladivostok Standard Time", "Asia/Vladivostok", "(GMT +10:00) Vladivostok"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("W. Australia Standard Time", "Australia/Perth", "(GMT +08:00) Perth"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("W. Central Africa Standard Time", "Africa/Lagos", "(GMT +01:00) West Central Africa"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("W. Europe Standard Time", "Europe/Berlin", "(GMT +01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("West Asia Standard Time", "Asia/Tashkent", "(GMT +05:00) Tashkent"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("West Pacific Standard Time", "Pacific/Port_Moresby", "(GMT +10:00) Guam, Port Moresby"));
        ZONE_MAPPINGS.add(new TimeZoneMapping("Yakutsk Standard Time", "Asia/Yakutsk", "(GMT +09:00) Yakutsk"));
    }

    private static final TimeZones INSTANCE = new TimeZones();

    public static TimeZones getInstance() {
        return INSTANCE;
    }

    private final List<TimeZoneWithDisplayNames> timeZones = new ArrayList<>();

    private TimeZones() {
        HashSet<String> availableIdsSet = new HashSet<>(Arrays.asList(TimeZone.getAvailableIDs()));

        for (TimeZoneMapping zoneMapping : ZONE_MAPPINGS) {
            String id = zoneMapping.getOlsonName();
            if (!availableIdsSet.contains(id)) {
                throw new IllegalStateException("Unknown ID [" + id + "]");
            }

            TimeZone timeZone = TimeZone.getTimeZone(id);
            timeZones.add(new TimeZoneWithDisplayNames(timeZone, zoneMapping.getWindowsDisplayName(),
                    zoneMapping.getWindowsStandardName()));
        }

        timeZones.sort((a, b) -> {
            int diff = a.getTimeZone().getRawOffset() - b.getTimeZone().getRawOffset();
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else {
                return a.getDisplayName().compareTo(b.getDisplayName());
            }
        });
    }

    public List<TimeZoneWithDisplayNames> getTimeZones() {
        return timeZones;
    }

    public static final class TimeZoneWithDisplayNames {

        private final TimeZone timeZone;
        private final String displayName;
        private final String standardDisplayName;

        public TimeZoneWithDisplayNames(TimeZone timeZone, String displayName,
                                        String standardDisplayName) {
            this.timeZone = timeZone;
            this.displayName = displayName;
            this.standardDisplayName = standardDisplayName;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getStandardDisplayName() {
            return standardDisplayName;
        }
    }

    private static final class TimeZoneMapping {

        private final String windowsStandardName;
        private final String olsonName;
        private final String windowsDisplayName;

        public TimeZoneMapping(String windowsStandardName, String olsonName,
                               String windowsDisplayName) {
            this.windowsStandardName = windowsStandardName;
            this.olsonName = olsonName;
            this.windowsDisplayName = windowsDisplayName;
        }

        public String getWindowsStandardName() {
            return windowsStandardName;
        }

        public String getOlsonName() {
            return olsonName;
        }

        public String getWindowsDisplayName() {
            return windowsDisplayName;
        }
    }

}
