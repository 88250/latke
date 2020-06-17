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

import freemarker.core.TemplateElement;
import freemarker.template.Template;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Enumeration;

/**
 * Utilities of <a href="http://www.freemarker.org">FreeMarker</a> template engine.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Sep 26, 2018
 */
public final class Templates {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Templates.class);

    /**
     * Private constructor.
     */
    private Templates() {
    }

    /**
     * Determines whether exists a variable specified by the given expression
     * in the specified template.
     *
     * @param template   the specified template
     * @param expression the given expression, for example,
     *                   "${aVariable}", "&lt;#list recentComments as comment&gt;"
     * @return {@code true} if it exists, returns {@code false} otherwise
     */
    public static boolean hasExpression(final Template template, final String expression) {
        final TemplateElement rootTreeNode = template.getRootTreeNode();

        return hasExpression(template, expression, rootTreeNode);
    }

    /**
     * Determines whether the specified expression exists in the specified
     * element (includes its children) of the specified template.
     *
     * @param template        the specified template
     * @param expression      the specified expression
     * @param templateElement the specified element
     * @return {@code true} if it exists, returns {@code false} otherwise
     */
    private static boolean hasExpression(final Template template, final String expression, final TemplateElement templateElement) {
        final String canonicalForm = templateElement.getCanonicalForm();

        if (canonicalForm.startsWith(expression)) {
            LOGGER.log(Level.TRACE, "Template has expression[nodeName={}, expression={}]",
                    templateElement.getNodeName(), expression);

            return true;
        }

        final Enumeration<TemplateElement> children = templateElement.children();
        while (children.hasMoreElements()) {
            final TemplateElement nextElement = children.nextElement();

            if (hasExpression(template, expression, nextElement)) {
                return true;
            }
        }

        return false;
    }
}
