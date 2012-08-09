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
package org.b3log.latke.image.local;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.b3log.latke.image.Image;
import org.b3log.latke.image.ImageService;

/**
 * Image service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 16, 2011
 */
public final class LocalImageService implements ImageService {

    @Override
    public Image makeImage(final byte[] data) {
        final Image ret = new Image();
        ret.setData(data);

        return ret;
    }

    @Override
    public Image makeImage(final List<Image> images) {
        if (null == images || images.isEmpty()) {
            return null;
        }

        try {
            final Image firstImage = images.get(0);
            BufferedImage tmp = ImageIO.read(new ByteArrayInputStream(firstImage.getData()));

            for (int i = 1; i < images.size(); i++) {
                final Image image = images.get(i);

                final byte[] data = image.getData();
                final BufferedImage awtImage = ImageIO.read(new ByteArrayInputStream(data));

                tmp = splice(tmp, awtImage);
            }

            final Image ret = new Image();

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(tmp, "PNG", byteArrayOutputStream);

            final byte[] data = byteArrayOutputStream.toByteArray();
            ret.setData(data);

            return ret;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Splices the specified image1 and image2 horizontally.
     * 
     * @param image1 the specified image1
     * @param image2 the specified image2
     * @return the spliced image
     */
    private static BufferedImage splice(final java.awt.Image image1, final java.awt.Image image2) {
        final int[][] size = {{image1.getWidth(null), image1.getHeight(null)},
                              {image2.getWidth(null), image2.getHeight(null)}};

        final int width = size[0][0] + size[1][0];
        final int height = Math.max(size[0][1], size[1][1]);

        final BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = ret.createGraphics();
        g2.drawImage(image1, 0, 0, null);
        g2.drawImage(image2, size[0][0], 0, null);

        return ret;
    }
}
