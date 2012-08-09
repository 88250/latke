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
package org.b3log.latke.servlet.renderer.freemarker;

import org.b3log.latke.servlet.HTTPRequestContext;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 * 
 * <p>Do nothing after render.</p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 2, 2011
 */
public final class FreeMarkerRenderer extends AbstractFreeMarkerRenderer {

    @Override
    protected void beforeRender(final HTTPRequestContext context) throws Exception {
    }

    @Override
    protected void afterRender(final HTTPRequestContext context) throws Exception {
    }
}
