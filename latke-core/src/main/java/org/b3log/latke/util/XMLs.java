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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * XML utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 5, 2018
 * @since 2.4.4
 */
public final class XMLs {

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(XMLs.class);

    /**
     * Returns pretty print of the specified xml string.
     *
     * @param xml the specified xml string
     * @return the pretty print of the specified xml string
     */
    public static String format(final String xml) {
        try {
            final DocumentBuilder db = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            final Document doc = db.parse(new InputSource(new StringReader(xml)));
            final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            final StreamResult result = new StreamResult(new StringWriter());
            final DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Formats pretty XML failed: " + e.getMessage());

            return xml;
        }
    }

    /**
     * Private constructor.
     */
    private XMLs() {
    }
}
