/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
package org.b3log.latke.image;

import org.b3log.latke.logging.Logger;

/**
 * Image service factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Jul 5, 2017
 */
public final class ImageServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ImageServiceFactory.class);

    /**
     * Image service.
     */
    private static final ImageService IMAGE_SERVICE;

    static {
        LOGGER.info("Constructing image service....");

        try {
            final Class<ImageService> serviceClass = (Class<ImageService>) Class.forName(
                    "org.b3log.latke.image.local.LocalImageService");
            IMAGE_SERVICE = serviceClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize image service!", e);
        }

        LOGGER.info("Constructed image service");
    }

    /**
     * Private default constructor.
     */
    private ImageServiceFactory() {
    }

    /**
     * Gets image service.
     *
     * @return image service
     */
    public static ImageService getImageService() {
        return IMAGE_SERVICE;
    }
}
