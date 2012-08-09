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
package org.b3log.latke.plugin;

import java.util.Map;

/**
 * View load event data.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 11, 2011
 */
public final class ViewLoadEventData {

    /**
     * Name of the template file.
     */
    private String viewName;
    /**
     * Data model.
     */
    private Map<String, Object> dataModel;

    /**
     * Gets the data model.
     * 
     * @return data model
     */
    public Map<String, Object> getDataModel() {
        return dataModel;
    }

    /**
     * Sets the data model with the specified data model.
     * 
     * @param dataModel the specified data model
     */
    public void setDataModel(final Map<String, Object> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Gets the name of the template file.
     * 
     * @return name of the template file
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Sets the name of the template file with the specified name.
     * 
     * @param viewName the specified name
     */
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
}
