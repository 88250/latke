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
package org.b3log.latke.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A standard object serializer to serialize and deserialize for an object that
 * implemented {@link java.io.Serializable}.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 13, 2010
 * @see Serializable
 */
public final class Serializer {

    /**
     * Serializes an object using default serialization.
     * 
     * @param obj an object need to serialize
     * @return the byte array of the serialized object
     * @throws IOException io exception
     */
    public static byte[] serialize(final Serializable obj) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);

        try {
            oos.writeObject(obj);
        } finally {
            oos.close();
        }
        return baos.toByteArray();
    }

    /**
     * Uses default de-serialization to turn a byte array into an object.
     *
     * @param data the byte array need to be converted
     * @return the converted object
     * @throws IOException io exception
     * @throws ClassNotFoundException class not found exception
     */
    public static Object deserialize(final byte[] data) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final BufferedInputStream bis = new BufferedInputStream(bais);
        final ObjectInputStream ois = new ObjectInputStream(bis);

        try {
            try {
                return ois.readObject();
            } catch (final IOException e) {
                throw e;
            } catch (final ClassNotFoundException e) {
                throw e;
            }
        } finally {
            ois.close();
        }
    }

    /**
     * Private default constructor.
     */
    private Serializer() {
    }
}
