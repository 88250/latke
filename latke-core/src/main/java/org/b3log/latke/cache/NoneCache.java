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
package org.b3log.latke.cache;

import org.json.JSONObject;

import java.util.Collection;

/**
 * None cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Mar 26, 2019
 * @since 2.3.13
 */
public final class NoneCache extends AbstractCache {

    @Override
    public boolean contains(final String key) {
        return false;
    }

    @Override
    public void put(final String key, final JSONObject value) {
    }

    @Override
    public JSONObject get(final String key) {
        return null;
    }

    @Override
    public void remove(final String key) {
    }

    @Override
    public void remove(final Collection<String> keys) {
    }

    @Override
    public void clear() {
    }
}
