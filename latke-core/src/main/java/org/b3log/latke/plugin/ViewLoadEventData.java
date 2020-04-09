/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.plugin;


import java.util.Map;


/**
 * View load event data.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
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
