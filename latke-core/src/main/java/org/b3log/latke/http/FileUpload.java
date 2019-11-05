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
package org.b3log.latke.http;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

/**
 * HTTP File upload.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 5, 2019
 * @since 3.0.0
 */
public class FileUpload {

    private static final Logger LOGGER = Logger.getLogger(FileUpload.class);

    io.netty.handler.codec.http.multipart.FileUpload fileUpload;

    public byte[] getData() {
        try {
            return fileUpload.get();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get file upload data fialed", e);
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
