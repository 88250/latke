/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import java.util.Map;

import org.b3log.latke.servlet.HTTPRequestContext;


/**
 * the default plugin for which do not need interact with the server end.
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.1.0.0, Jan 23, 2013
 */
@SuppressWarnings("serial")
public class NotInteractivePlugin extends AbstractPlugin {

    @Override
    public void prePlug(final HTTPRequestContext context, final  Map<String, Object> args) {}

    @Override
    public void postPlug(final Map<String, Object> dataModel, final HTTPRequestContext context, final Object ret) {}

    @Override
    public void start() {}

    @Override
    public void stop() {}

}
