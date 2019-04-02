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
package org.b3log.latke.plugin;

import org.b3log.latke.servlet.RequestContext;

import java.util.Map;

/**
 * The default plugin for which do not need interact with the server end.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, May 31, 2014
 */
@SuppressWarnings("serial")
public class NotInteractivePlugin extends AbstractPlugin {

    @Override
    public void prePlug(final RequestContext context) {
    }

    @Override
    public void postPlug(final Map<String, Object> dataModel, final RequestContext context) {
    }
}
