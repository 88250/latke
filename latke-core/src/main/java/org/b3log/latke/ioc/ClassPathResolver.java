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
package org.b3log.latke.ioc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Resolver for scanning the classpath.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, Jun 20, 2020
 */
public final class ClassPathResolver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ClassPathResolver.class);

    /**
     * Separator between JAR URL and file path within the JAR.
     */
    private static final String JAR_URL_SEPARATOR = "!/";

    /**
     * URL prefix for loading from the file system: "file:".
     */
    private static final String FILE_URL_PREFIX = "file:";

    /**
     * Private constructor.
     */
    private ClassPathResolver() {
    }

    /**
     * Gets all URLs (resources) under the pattern.
     *
     * @param locationPattern the pattern of classPath (a ant-style string)
     * @return all URLS
     */
    public static Set<URL> getResources(final String locationPattern) {
        final Set<URL> result = new HashSet<>();
        final String scanRootPath = getRootPath(locationPattern);
        final String subPattern = locationPattern.substring(scanRootPath.length());
        final Set<URL> rootDirResources = getResourcesFromRoot(scanRootPath);
        for (final URL rootDirResource : rootDirResources) {
            LOGGER.log(Level.DEBUG, "RootDirResource [protocol={}, path={}]", rootDirResource.getProtocol(), rootDirResource.getPath());
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
     * if "/context/** / *.xml" should get the result "/context/"
     * </p>
     *
     * @param locationPattern locationPattern
     * @return the RootPath string.
     */
    private static String getRootPath(final String locationPattern) {
        int rootDirEnd = locationPattern.length();
        while (AntPathMatcher.isPattern(locationPattern.substring(0, rootDirEnd))) {
            rootDirEnd = locationPattern.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        return locationPattern.substring(0, rootDirEnd);
    }

    /**
     * the URLS under Root path ,each of which we should scan from.
     *
     * @param rootPath rootPath
     * @return the URLS under the Root path
     */
    private static Set<URL> getResourcesFromRoot(final String rootPath) {
        final Set<URL> rets = new LinkedHashSet<>();
        String path = rootPath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        try {
            final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                rets.add(url);
            }
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "get the ROOT Rescources error", e);
        }
        return rets;
    }

    /**
     * check if the URL of the Resource is a JAR resource.
     *
     * @param rootDirResource rootDirResource
     * @return isJAR
     */
    private static boolean isJarURL(final URL rootDirResource) {
        final String protocol = rootDirResource.getProtocol();
        // Determine whether the given URL points to a resource in a jar file, that is, has protocol "jar", "zip",
        // "wsjar" or "code-source".
        //
        // "zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively, but can be treated like
        // jar files. The same applies to "code-source" URLs on Oracle OC4J, provided that the path contains a jar
        // separator.
        return "jar".equals(protocol) || "zip".equals(protocol) || "wsjar".equals(protocol)
                || ("code-source".equals(protocol) && rootDirResource.getPath().contains(JAR_URL_SEPARATOR));
    }

    /**
     * scan the jar to get the URLS of the Classes.
     *
     * @param rootDirResource which is "Jar"
     * @param subPattern      subPattern
     * @return the URLs of all the matched classes
     */
    private static Collection<? extends URL> doFindPathMatchingJarResources(final URL rootDirResource, final String subPattern) {
        final Set<URL> result = new LinkedHashSet<>();

        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        URLConnection con;
        boolean newJarFile = false;

        try {
            con = rootDirResource.openConnection();
            if (con instanceof JarURLConnection) {
                final JarURLConnection jarCon = (JarURLConnection) con;
                jarCon.setUseCaches(false);
                jarFile = jarCon.getJarFile();
                final JarEntry jarEntry = jarCon.getJarEntry();
                rootEntryPath = jarEntry != null ? jarEntry.getName() : "";
            } else {
                // No JarURLConnection -> need to resort to URL file parsing.
                // We'll assume URLs of the format "jar:path!/entry", with the
                // protocol
                // being arbitrary as long as following the entry format.
                // We'll also handle paths with and without leading "file:"
                // prefix.
                final String urlFile = rootDirResource.getFile();
                final int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);

                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
                    jarFile = getJarFile(jarFileUrl);
                } else {
                    jarFile = new JarFile(urlFile);
                    rootEntryPath = "";
                }
                newJarFile = true;
            }
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "reslove jar File error", e);
            return result;
        }

        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper
                // matching.
                // The Sun JRE does not return a slash here, but BEA JRockit
                // does.
                rootEntryPath = rootEntryPath + "/";
            }
            for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                final JarEntry entry = entries.nextElement();
                final String entryPath = entry.getName();

                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());

                    if (AntPathMatcher.match(subPattern, relativePath)) {
                        if (relativePath.startsWith("/")) {
                            relativePath = relativePath.substring(1);
                        }
                        result.add(new URL(rootDirResource, relativePath));
                    }
                }
            }
            return result;
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "parse the JarFile error", e);
        } finally {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile) {
                try {
                    jarFile.close();
                } catch (final IOException e) {
                    LOGGER.log(Level.WARN, " occur error when closing jarFile", e);
                }
            }
        }
        return result;
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     *
     * @param jarFileUrl jarFileUrl
     * @return the JarFile
     * @throws IOException IOException
     */
    private static JarFile getJarFile(final String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (final URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever
                // happen).
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    /**
     * scan the system file to get the URLS of the Classes.
     *
     * @param rootDirResource rootDirResource which is in File System
     * @param subPattern      subPattern
     * @return the URLs of all the matched classes
     */
    private static Collection<? extends URL> doFindPathMatchingFileResources(final URL rootDirResource, final String subPattern) {

        File rootFile;
        final Set<URL> rets = new LinkedHashSet<>();

        try {
            rootFile = new File(rootDirResource.toURI());
        } catch (final URISyntaxException e) {
            LOGGER.log(Level.ERROR, "cat not resolve the rootFile", e);
            throw new RuntimeException("cat not resolve the rootFile", e);
        }
        String fullPattern = StringUtils.replace(rootFile.getAbsolutePath(), File.separator, "/");

        if (!subPattern.startsWith("/")) {
            fullPattern += "/";
        }
        final String filePattern = fullPattern + StringUtils.replace(subPattern, File.separator, "/");

        @SuppressWarnings("unchecked") final Collection<File> files = FileUtils.listFiles(rootFile, new IOFileFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return true;
            }

            @Override
            public boolean accept(final File file) {
                if (file.isDirectory()) {
                    return false;
                }
                return AntPathMatcher.match(filePattern, StringUtils.replace(file.getAbsolutePath(), File.separator, "/"));
            }
        }, TrueFileFilter.INSTANCE);

        try {
            for (final File file : files) {
                rets.add(file.toURI().toURL());
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "convert file to URL error", e);
            throw new RuntimeException("convert file to URL error", e);
        }
        return rets;
    }

    /**
     * Create a URI instance for the given location String, replacing spaces with "%20" quotes first.
     *
     * @param location the location String to convert into a URI instance
     * @return the URI instance
     * @throws URISyntaxException if the location wasn't a valid URI
     */
    private static URI toURI(final String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
}
