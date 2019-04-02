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

import freemarker.core.TemplateElement;
import freemarker.template.Template;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(Templates.class);

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
            LOGGER.log(Level.TRACE, "Template has expression[nodeName={0}, expression={1}]",
                    new Object[]{templateElement.getNodeName(), expression});

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
