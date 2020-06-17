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
package org.b3log.latke.http;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP File upload.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 5, 2019
 * @since 3.0.0
 */
public class FileUpload {

    private static final Logger LOGGER = LogManager.getLogger(FileUpload.class);

    io.netty.handler.codec.http.multipart.FileUpload fileUpload;

    public byte[] getData() {
        try {
            return fileUpload.get();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get file upload data failed", e);
            return null;
        }
    }

    public String getName() {
        return fileUpload.getName();
    }

    public String getFilename() {
        return fileUpload.getFilename();
    }

    public String getContentType() {
        return fileUpload.getContentType();
    }

}
