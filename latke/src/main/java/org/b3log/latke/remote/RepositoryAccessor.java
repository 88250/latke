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
package org.b3log.latke.remote;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Accesses repository via HTTP protocol.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Apr 22, 2012
 */
@RequestProcessor
public final class RepositoryAccessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepositoryAccessor.class.getName());

    /**
     * Gets whether repositories is writable.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/repositories/writable?<em>userName=xxx&password=xxx</em><br/>
     * All parameters are required.
     * </p>
     * 
     * <p>
     * Renders response like the following: 
     * <pre>
     * {
     *     "sc":200,
     *     "writable": true,
     *     "msg":"Gets repositories writable[true]"
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/repositories/writable", method = HTTPRequestMethod.GET)
    public void getRepositoriesWritable(final HTTPRequestContext context, final HttpServletRequest request,
                                        final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        if (!authSucc(request, jsonObject)) {
            return;
        }

        final boolean writable = Repositories.getReposirotiesWritable();

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Gets repositories writable[" + writable + "]");
        jsonObject.put("writable", writable);
    }

    /**
     * Sets whether repositories is writable.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/repositories/writable?<em>userName=xxx&password=xxx&writable=true</em><br/>
     * All parameters are required.
     * </p>
     * 
     * <p>
     * Renders response like the following: 
     * <pre>
     * {
     *     "sc":200,
     *     "msg":"Sets repositories writable[true]"
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/repositories/writable", method = HTTPRequestMethod.PUT)
    public void setRepositoriesWritable(final HTTPRequestContext context, final HttpServletRequest request,
                                        final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        final String writable = request.getParameter("writable");
        if (!"true".equals(writable) && !"false".equals(writable)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[writable], optional value is [true] or [false]");

            return;
        }

        if (!authSucc(request, jsonObject)) {
            return;
        }

        Repositories.setRepositoriesWritable(Boolean.parseBoolean(writable));

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Sets repositories writable[" + writable + "]");
    }

    /**
     * Gets repository names.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/repository/names?<em>userName=xxx&password=xxx</em><br/>
     * All parameters are required.
     * </p>
     * 
     * <p>
     * Renders response like the following: 
     * <pre>
     * {
     *     "sc":200,
     *     "msg":"Got data",
     *     "repositoryNames" : [
     *         "repository1", "repository2", ....
     *       ] 
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/repository/names", method = HTTPRequestMethod.GET)
    public void getRepositoryNames(final HTTPRequestContext context, final HttpServletRequest request,
                                   final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Got data");

        if (!authSucc(request, jsonObject)) {
            return;
        }

        jsonObject.put("repositoryNames", Repositories.getRepositoryNames());
    }

    /**
     * Gets repository data.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/repository/data?<em>userName=xxx&password=xxx&repositoryName=xxx&pageNum=xxx&pageSize=xxx</em><br/>
     * All parameters are required.
     * </p>
     * 
     * <p>
     * Renders response like the following:
     * <pre>
     * {
     *   "sc":200,
     *   "msg":"Got data",
     *   "pagination":{
     *      "paginationPageCount":11
     *   },
     *   "rslts":[{}, {}, ....]
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/repository/data", method = HTTPRequestMethod.GET)
    public void getData(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Got data");

        if (badGetDataRequest(request, jsonObject) || !authSucc(request, jsonObject)) {
            return;
        }

        final String repositoryName = request.getParameter("repositoryName");
        final Repository repository = Repositories.getRepository(repositoryName);
        if (null == repository) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Not found repository[name=" + repositoryName + "]");

            return;
        }

        final boolean cacheEnabled = repository.isCacheEnabled();

        repository.setCacheEnabled(false);

        final Query query = new Query().setCurrentPageNum(Integer.valueOf(request.getParameter("pageNum"))).
                setPageSize(Integer.valueOf(request.getParameter("pageSize")));

        try {
            final JSONObject result = repository.get(query);
            final JSONObject pagination = result.getJSONObject(Pagination.PAGINATION);
            final JSONArray data = result.getJSONArray(Keys.RESULTS);

            jsonObject.put(Pagination.PAGINATION, pagination);
            jsonObject.put(Keys.RESULTS, data);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets data failed", e);

            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonObject.put(Keys.MSG, "Gets data failed[errorMsg=" + e.getMessage() + "]");
        } finally {
            repository.setCacheEnabled(cacheEnabled);
        }
    }

    /**
     * Puts data to repository.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/repository/data?<em>userName=xxx&password=xxx&repositoryName=xxx</em><br/>
     * All parameters are required.
     * </p>
     * 
     * <p>
     * The post body, for example, "data": {....} or [] // JSON object or JSON array, content of the backup file
     * </p>
     * 
     * <p>
     * Renders response like the following:
     * <pre>
     * {
     *   "sc":200,
     *   "msg":"Put data"
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/repository/data", method = HTTPRequestMethod.POST)
    public void putData(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Put data");

        if (badPutDataRequest(request, jsonObject) || !authSucc(request, jsonObject)) {
            return;
        }

        final String repositoryName = request.getParameter("repositoryName");
        Repository repository = Repositories.getRepository(repositoryName);
        if (null == repository) {
            repository = new AbstractRepository(repositoryName) {
            };
        }

        repository.setCacheEnabled(false);

        final Transaction transaction = repository.beginTransaction();
        try {
            final String dataContent = request.getParameter("data").trim();
            final JSONArray data = new JSONArray(dataContent);
            for (int i = 0; i < data.length(); i++) {
                final JSONObject record = data.getJSONObject(i);
                repository.add(record);
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Gets data failed", e);

            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonObject.put(Keys.MSG, "Gets data failed[errorMsg=" + e.getMessage() + "]");
        } finally {
            repository.setCacheEnabled(true);
        }
    }

    /**
     * Determines whether the specified request is authenticated.
     * 
     * <p>
     * If the specified request is unauthenticated, puts {@link Keys#STATUS_CODE sc} and {@link Keys#MSG msg}
     * into the specified json object to render.
     * </p>
     * 
     * @param request the specified request
     * @param jsonObject the specified json object
     * @return {@code true} if authenticated, returns {@code false} otherwise
     */
    private boolean authSucc(final HttpServletRequest request, final JSONObject jsonObject) {
        if (!Latkes.isRemoteEnabled()) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_NOT_IMPLEMENTED);
            jsonObject.put(Keys.MSG, "Latke remote interfaces are disabled");
            return false;
        }

        final String userName = request.getParameter("userName");
        final String password = request.getParameter("password");

        if (Strings.isEmptyOrNull(userName)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[userName]");
            return false;
        }

        if (Strings.isEmptyOrNull(password)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[password]");
            return false;
        }

        final String repositoryAccessorUserName = Latkes.getRemoteProperty("repositoryAccessor.userName");
        final String repositoryAccessorPassword = Latkes.getRemoteProperty("repositoryAccessor.password");

        if (userName.equals(repositoryAccessorUserName) && password.equals(repositoryAccessorPassword)) {
            return true;
        }

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
        jsonObject.put(Keys.MSG, "Auth failed[userName=" + userName + ", password=" + password + "]");

        return false;
    }

    /**
     * Determines whether the specified get data request is bad.
     * 
     * <p>
     * If the specified request is bad, puts {@link Keys#STATUS_CODE sc} and {@link Keys#MSG msg}
     * into the specified json object to render.
     * </p>
     * 
     * @param request the specified request
     * @param jsonObject the specified jsonObject
     * @return {@code true} if it is bad, returns {@code false} otherwise
     */
    private boolean badGetDataRequest(final HttpServletRequest request, final JSONObject jsonObject) {
        final String repositoryName = request.getParameter("repositoryName");
        final String pageNumString = request.getParameter("pageNum");
        final String pageSizeString = request.getParameter("pageSize");

        if (Strings.isEmptyOrNull(repositoryName)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[repositoryName]");
            return true;
        }

        if (Strings.isEmptyOrNull(pageNumString)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[pageNum]");
            return true;
        }

        if (Strings.isEmptyOrNull(pageSizeString)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[pageSize]");
            return true;
        }

        try {
            Integer.parseInt(pageNumString);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Parameter[pageNum] must be a integer");
            return true;
        }

        try {
            Integer.parseInt(pageSizeString);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Parameter[pageSize] must be a integer");
            return true;
        }

        return false;
    }

    /**
     * Determines whether the specified put data request is bad.
     * 
     * <p>
     * If the specified request is bad, puts {@link Keys#STATUS_CODE sc} and {@link Keys#MSG msg}
     * into the specified json object to render.
     * </p>
     * 
     * @param request the specified request
     * @param jsonObject the specified jsonObject
     * @return {@code true} if it is bad, returns {@code false} otherwise
     */
    private boolean badPutDataRequest(final HttpServletRequest request, final JSONObject jsonObject) {
        final String repositoryName = request.getParameter("repositoryName");
        if (Strings.isEmptyOrNull(repositoryName)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[repositoryName]");
            return true;
        }

        final String dataContent = request.getParameter("data");
        if (Strings.isEmptyOrNull(dataContent)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[data]");
            return true;
        }

        try {
            new JSONArray(dataContent);
        } catch (final JSONException e) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Parameter[data] must be a JSON object or a JSON array");
            return true;
        }

        return false;
    }
}
