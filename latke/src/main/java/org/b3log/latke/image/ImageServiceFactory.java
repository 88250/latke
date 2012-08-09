/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;

/**
 * Image service factory.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public final class ImageServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ImageServiceFactory.class.getName());
    /**
     * Image service.
     */
    private static final ImageService IMAGE_SERVICE;

    static {
        LOGGER.info("Constructing Image Service....");

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        try {
            Class<ImageService> serviceClass = null;

            switch (runtimeEnv) {
                case LOCAL:
                    serviceClass =
                            (Class<ImageService>) Class.forName("org.b3log.latke.image.local.LocalImageService");
                    IMAGE_SERVICE = serviceClass.newInstance();
                    break;
                case GAE:
                    serviceClass =
                            (Class<ImageService>) Class.forName("org.b3log.latke.image.gae.GAEImageService");
                    IMAGE_SERVICE = serviceClass.newInstance();
                    break;
                default:
                    throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize Image Service!", e);
        }

        LOGGER.info("Constructed Image Service");
    }

    /**
     * Gets image service.
     * 
     * @return image service
     */
    public static ImageService getImageService() {
        return IMAGE_SERVICE;
    }

    /**
     * Private default constructor.
     */
    private ImageServiceFactory() {
    }
}
