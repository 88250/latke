/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.image.gae;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import java.util.ArrayList;
import java.util.List;
import org.b3log.latke.image.Image;
import org.b3log.latke.image.ImageService;

/**
 * Google App Engine image service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 24, 2011
 */
public final class GAEImageService implements ImageService {

    /**
     * Images service.
     */
    private static final ImagesService SVC = ImagesServiceFactory.getImagesService();

    @Override
    public Image makeImage(final byte[] data) {
        final Image ret = new Image();
        ret.setData(data);

        return ret;
    }

    @Override
    public Image makeImage(final List<Image> images) {
        final List<Composite> composites = new ArrayList<Composite>();

        int width = 0;
        int height = 0;
        final int length = images.size();
        for (int i = 0; i < length; i++) {
            final Image image = images.get(i);
            final byte[] imageData = image.getData();
            final com.google.appengine.api.images.Image gaeImage = ImagesServiceFactory.makeImage(imageData);

            final Composite composite = ImagesServiceFactory.makeComposite(
                    gaeImage, i * gaeImage.getWidth(), 0, 1.0F, Composite.Anchor.TOP_LEFT);
            composites.add(composite);

            if (i == length - 1) { // Using the last clip as the dimension of eatch one
                width = gaeImage.getWidth();
                height = gaeImage.getHeight();
            }
        }

        final com.google.appengine.api.images.Image gaeImage = SVC.composite(composites, width * length, height, 0);

        return makeImage(gaeImage.getImageData());
    }
}
