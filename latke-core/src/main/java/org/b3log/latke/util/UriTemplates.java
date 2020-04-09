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

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * URI template utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 4, 2018
 * @since 2.4.34
 */
public final class UriTemplates {

    /**
     * Resolves the specified URI with the specified URI template.
     *
     * @param uri         the specified URI
     * @param uriTemplate the specified URI template
     * @return resolved mappings of name and argument, returns {@code null} if failed
     */
    public static Map<String, String> resolve(final String uri, final String uriTemplate) {
        final String[] parts = URLs.decode(uri).split("/");
        final String[] templateParts = uriTemplate.split("/");
        if (parts.length != templateParts.length) {
            return null;
        }

        final Map<String, String> ret = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            final String templatePart = templateParts[i];
            if (part.equals(templatePart)) {
                continue;
            }

            String name = StringUtils.substringBetween(templatePart, "{", "}");
            if (StringUtils.isBlank(name)) {
                return null;
            }

            final String templatePartTmp = StringUtils.replace(templatePart, "{" + name + "}", "");
            final String arg = StringUtils.replace(part, templatePartTmp, "");

            ret.put(name, arg);
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private UriTemplates() {
    }
}
