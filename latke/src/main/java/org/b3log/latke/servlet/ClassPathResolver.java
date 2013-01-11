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
package org.b3log.latke.servlet;


import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * the Resolver for scanning the classpath.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.1.0, Jan 11, 2013
 */
public class ClassPathResolver {

    /**
     * get all URL(Resource) under the pattern. 
     * @param locationPattern  the pattern of classPath 
     * @return all URLS
     */
    public Set<URL> getResources(final String locationPattern) {

        final Set<URL> result = new HashSet<URL>();

        final String scanRootPath = getRootPath(locationPattern);
        final String subPattern = locationPattern.substring(scanRootPath.length());
        final Set<URL> rootDirResources = getResourcesFromRoot(scanRootPath);

        for (URL rootDirResource : rootDirResources) {
            if (isJarURL(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }

        return result;

    }

    /**
     * get rootPath from locationPattern.
     * <p>
     *      if "/context/** / *.xml"
     *      should get the result "/context/"
     * </p> 
     * @param locationPattern locationPattern
     * @return the RootPath string.
     */
    private String getRootPath(final String locationPattern) {
        return null;
    }

    /**
     * the URLS under Root path ,each of which we should scan from.    
     * @param rootPath  rootPath
     * @return the URLS under the Root path
     */
    private Set<URL> getResourcesFromRoot(final String rootPath) {
        return null;
    }

    /**
     * check if the URL of the Rousource is a JAR resource.
     * @param rootDirResource  rootDirResource
     * @return isJAR
     */
    private boolean isJarURL(final URL rootDirResource) {
        return false;
    }

    /**
     * scan the jar to get the URLS of the Classes.
     * @param rootDirResource which is "Jar"
     * @param subPattern subPattern
     * @return the URLs of all the matched classes 
     */
    private Collection<? extends URL> doFindPathMatchingJarResources(final URL rootDirResource, final String subPattern) {
        return null;
    }

    /**
     * scan the system file to get the URLS of the Classes.
     * @param rootDirResource rootDirResource which is in File System
     * @param subPattern subPattern
     * @return the URLs of all the matched classes 
     */
    private Collection<? extends URL> doFindPathMatchingFileResources(final URL rootDirResource, final String subPattern) {
        return null;
    }

}
