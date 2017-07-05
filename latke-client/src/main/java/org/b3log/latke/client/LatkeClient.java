/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.client;


import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Latke client.
 * 
 * <p>
 * See the design document <a href="https://docs.google.com/document/d/1IQkkUuaCPNHc_Wjw_5mNwPKUX8TpkAGCGqUaAErOTLo/edit">
 * 《B3log 数据备份与恢复》</a> for more details.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Jan 19, 2013
 */
public final class LatkeClient {

    /**
     * Client version.
     */
    private static final String VERSION = "0.1.0";

    /**
     * Gets repository names.
     */
    private static final String GET_REPOSITORY_NAMES = "/latke/remote/repository/names";

    /**
     * Sets repositories writable.
     */
    private static final String SET_REPOSITORIES_WRITABLE = "/latke/remote/repositories/writable";

    /**
     * Create tables.
     */
    private static final String CREATE_TABLES = "/latke/remote/repository/tables";

    /**
     * Gets data.
     */
    private static final String GET_DATA = "/latke/remote/repository/data";

    /**
     * Puts data.
     */
    private static final String PUT_DATA = "/latke/remote/repository/data";

    /**
     * Server address, starts with http://.
     */
    private static String serverAddress = "";

    /**
     * Backup directory.
     */
    private static File backupDir;

    /**
     * User name.
     */
    private static String userName = "";

    /**
     * Password.
     */
    private static String password = "";

    /**
     * Verbose.
     */
    private static boolean verbose;

    /**
     * Backup page size.
     */
    private static final String PAGE_SIZE = "10";

    /**
     * Main entry.
     * 
     * @param args the specified command line arguments
     * @throws Exception exception 
     */
    public static void main(String[] args) throws Exception {
        // Backup Test:      
//         args = new String[] {
//         "-h", "-backup", "-repository_names", "-verbose", "-s", "chevo2xs.appspot.com:80", "-u", "zane", "-p", "xxx", "-backup_dir",
//         "C:/b3log_backup", "-w", "true"};

        args = new String[] {
            "-h", "-restore", "-create_tables", "-verbose", "-s", "localhost:8080", "-u", "zane", "-p", "xxx", "-backup_dir",
            "C:/b3log_backup_devapi"};

        final Options options = getOptions();

        final CommandLineParser parser = new PosixParser();

        try {
            final CommandLine cmd = parser.parse(options, args);

            serverAddress = cmd.getOptionValue("s");

            backupDir = new File(cmd.getOptionValue("backup_dir"));
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }

            userName = cmd.getOptionValue("u");

            if (cmd.hasOption("verbose")) {
                verbose = true;
            }

            password = cmd.getOptionValue("p");

            if (verbose) {
                System.out.println("Requesting server[" + serverAddress + "]");
            }

            final HttpClient httpClient = new DefaultHttpClient();

            if (cmd.hasOption("repository_names")) {
                getRepositoryNames();
            }

            if (cmd.hasOption("create_tables")) {
                final List<NameValuePair> qparams = new ArrayList<NameValuePair>();

                qparams.add(new BasicNameValuePair("userName", userName));
                qparams.add(new BasicNameValuePair("password", password));

                final URI uri = URIUtils.createURI("http", serverAddress, -1, CREATE_TABLES, URLEncodedUtils.format(qparams, "UTF-8"), null);
                final HttpPut request = new HttpPut();

                request.setURI(uri);

                if (verbose) {
                    System.out.println("Starting create tables");
                }

                final HttpResponse httpResponse = httpClient.execute(request);
                final InputStream contentStream = httpResponse.getEntity().getContent();
                final String content = IOUtils.toString(contentStream).trim();

                if (verbose) {
                    printResponse(content);
                }
            }

            if (cmd.hasOption("w")) {
                final String writable = cmd.getOptionValue("w");
                final List<NameValuePair> qparams = new ArrayList<NameValuePair>();

                qparams.add(new BasicNameValuePair("userName", userName));
                qparams.add(new BasicNameValuePair("password", password));

                qparams.add(new BasicNameValuePair("writable", writable));
                final URI uri = URIUtils.createURI("http", serverAddress, -1, SET_REPOSITORIES_WRITABLE,
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
                final HttpPut request = new HttpPut();

                request.setURI(uri);

                if (verbose) {
                    System.out.println("Setting repository writable[" + writable + "]");
                }

                final HttpResponse httpResponse = httpClient.execute(request);
                final InputStream contentStream = httpResponse.getEntity().getContent();
                final String content = IOUtils.toString(contentStream).trim();

                if (verbose) {
                    printResponse(content);
                }
            }

            if (cmd.hasOption("backup")) {
                System.out.println("Make sure you have disabled repository writes with [-w false], continue? (y)");
                final Scanner scanner = new Scanner(System.in);
                final String input = scanner.next();

                scanner.close();

                if (!"y".equals(input)) {
                    return;
                }

                if (verbose) {
                    System.out.println("Starting backup data");
                }

                final Set<String> repositoryNames = getRepositoryNames();

                for (final String repositoryName : repositoryNames) {
                    // You could specify repository manually 
                    // if (!"archiveDate_article".equals(repositoryName)) {
                    // continue;
                    // }

                    int requestPageNum = 1;

                    // Backup interrupt recovery
                    final List<File> backupFiles = getBackupFiles(repositoryName);

                    if (!backupFiles.isEmpty()) {
                        final File latestBackup = backupFiles.get(backupFiles.size() - 1);

                        final String latestPageSize = getBackupFileNameField(latestBackup.getName(), "${pageSize}");

                        if (!PAGE_SIZE.equals(latestPageSize)) {
                            // The 'latestPageSize' should be less or equal to 'PAGE_SIZE', if they are not the same that indicates 
                            // the latest backup file is the last of this repository, the repository backup had completed

                            if (verbose) {
                                System.out.println("Repository [" + repositoryName + "] backup have completed");
                            }

                            continue;
                        }

                        final String latestPageNum = getBackupFileNameField(latestBackup.getName(), "${pageNum}");

                        // Prepare for the next page to request
                        requestPageNum = Integer.parseInt(latestPageNum) + 1;

                        if (verbose) {
                            System.out.println("Start tot backup interrupt recovery [pageNum=" + requestPageNum + "]");
                        }
                    }

                    int totalPageCount = requestPageNum + 1;

                    for (; requestPageNum <= totalPageCount; requestPageNum++) {
                        final List<NameValuePair> params = new ArrayList<NameValuePair>();

                        params.add(new BasicNameValuePair("userName", userName));
                        params.add(new BasicNameValuePair("password", password));
                        params.add(new BasicNameValuePair("repositoryName", repositoryName));
                        params.add(new BasicNameValuePair("pageNum", String.valueOf(requestPageNum)));
                        params.add(new BasicNameValuePair("pageSize", PAGE_SIZE));
                        final URI uri = URIUtils.createURI("http", serverAddress, -1, GET_DATA, URLEncodedUtils.format(params, "UTF-8"),
                            null);
                        final HttpGet request = new HttpGet(uri);

                        if (verbose) {
                            System.out.println(
                                "Getting data from repository [" + repositoryName + "] with pagination [pageNum=" + requestPageNum
                                + ", pageSize=" + PAGE_SIZE + "]");
                        }

                        final HttpResponse httpResponse = httpClient.execute(request);
                        final InputStream contentStream = httpResponse.getEntity().getContent();
                        final String content = IOUtils.toString(contentStream, "UTF-8").trim();

                        contentStream.close();

                        if (verbose) {
                            printResponse(content);
                        }

                        final JSONObject resp = new JSONObject(content);
                        final JSONObject pagination = resp.getJSONObject("pagination");

                        totalPageCount = pagination.getInt("paginationPageCount");
                        final JSONArray results = resp.getJSONArray("rslts");

                        final String backupPath = backupDir.getPath() + File.separatorChar + repositoryName + File.separatorChar
                            + requestPageNum + '_' + results.length() + '_' + System.currentTimeMillis() + ".json";
                        final File backup = new File(backupPath);
                        final FileWriter fileWriter = new FileWriter(backup);

                        IOUtils.write(results.toString(), fileWriter);
                        fileWriter.close();

                        if (verbose) {
                            System.out.println("Backup file[path=" + backupPath + "]");
                        }
                    }
                }
            }

            if (cmd.hasOption("restore")) {
                System.out.println("Make sure you have enabled repository writes with [-w true], continue? (y)");
                final Scanner scanner = new Scanner(System.in);
                final String input = scanner.next();

                scanner.close();

                if (!"y".equals(input)) {
                    return;
                }

                if (verbose) {
                    System.out.println("Starting restore data");
                }

                final Set<String> repositoryNames = getRepositoryNamesFromBackupDir();

                for (final String repositoryName : repositoryNames) {
                    // You could specify repository manually 
                    // if (!"archiveDate_article".equals(repositoryName)) {
                    // continue;
                    // }

                    final List<File> backupFiles = getBackupFiles(repositoryName);

                    if (verbose) {
                        System.out.println("Restoring repository[" + repositoryName + ']');
                    }

                    for (final File backupFile : backupFiles) {
                        final FileReader backupFileReader = new FileReader(backupFile);
                        final String dataContent = IOUtils.toString(backupFileReader);

                        backupFileReader.close();

                        final List<NameValuePair> params = new ArrayList<NameValuePair>();

                        params.add(new BasicNameValuePair("userName", userName));
                        params.add(new BasicNameValuePair("password", password));
                        params.add(new BasicNameValuePair("repositoryName", repositoryName));
                        final URI uri = URIUtils.createURI("http", serverAddress, -1, PUT_DATA, URLEncodedUtils.format(params, "UTF-8"),
                            null);
                        final HttpPost request = new HttpPost(uri);

                        final List<NameValuePair> data = new ArrayList<NameValuePair>();

                        data.add(new BasicNameValuePair("data", dataContent));
                        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF-8");

                        request.setEntity(entity);

                        if (verbose) {
                            System.out.println("Data[" + dataContent + "]");
                        }

                        final HttpResponse httpResponse = httpClient.execute(request);
                        final InputStream contentStream = httpResponse.getEntity().getContent();
                        final String content = IOUtils.toString(contentStream, "UTF-8").trim();

                        contentStream.close();

                        if (verbose) {
                            printResponse(content);
                        }

                        final String pageNum = getBackupFileNameField(backupFile.getName(), "${pageNum}");
                        final String pageSize = getBackupFileNameField(backupFile.getName(), "${pageSize}");
                        final String backupTime = getBackupFileNameField(backupFile.getName(), "${backupTime}");

                        final String restoredPath = backupDir.getPath() + File.separatorChar + repositoryName + File.separatorChar + pageNum
                            + '_' + pageSize + '_' + backupTime + '_' + System.currentTimeMillis() + ".json";
                        final File restoredFile = new File(restoredPath);

                        backupFile.renameTo(restoredFile);

                        if (verbose) {
                            System.out.println("Backup file[path=" + restoredPath + "]");
                        }
                    }
                }
            }

            if (cmd.hasOption("v")) {
                System.out.println(VERSION);
            }

            if (cmd.hasOption("h")) {
                printHelp(options);
            }

            // final File backup = new File(backupDir.getPath() + File.separatorChar + repositoryName + pageNum + '_' + pageSize + '_'
            // + System.currentTimeMillis() + ".json");
            // final FileEntity fileEntity = new FileEntity(backup, "application/json; charset=\"UTF-8\"");

        } catch (final ParseException e) {
            System.err.println("Parsing args failed, caused by: " + e.getMessage());
            printHelp(options);
        } catch (final ConnectException e) {
            System.err.println("Connection refused");
        }
    }

    /**
     * Gets the backup file name filed value with the specified repository backup file name and field name.
     * 
     * <p>
     * A repository backup file (not restored yet) name: "1_5_1334889225650.json", ${pageNum}_${pageSize}_${backupTime}.json
     * </p>
     * 
     * <p>
     * A repository backup file (restored) name: "1_5_1334889225470_1334889225650.json", 
     * ${pageNum}_${pageSize}_${backupTime}_${restoreTime}.json
     * </p> 
     *
     * @param repositoryBackupFileName the specified repository backup file name
     * @param field the specified, for example ${pageNum}
     * @return backup file name filed value, returns {@code null} if not found
     */
    private static String getBackupFileNameField(final String repositoryBackupFileName, final String field) {
        final String[] fields = repositoryBackupFileName.split("_");

        if ("${pageNum}".equals(field) && fields.length > 0) {
            return fields[0];
        }

        if ("${pageSize}".equals(field) && fields.length > 1) {
            return fields[1];
        }

        if ("${backupTime}".equals(field) && fields.length > 2) {
            return StringUtils.substringBefore(fields[2], ".json");
        }

        if ("${restoreTime}".equals(field) && fields.length > 3) {
            return StringUtils.substringBefore(fields[3], ".json");
        }

        return null;
    }

    /**
     * Gets the backup files under a backup specified directory by the given repository name.
     * 
     * @param repositoryName the given repository name
     * @return backup files, returns an empty set if not found
     */
    private static List<File> getBackupFiles(final String repositoryName) {
        final String backupRepositoryPath = backupDir.getPath() + File.separatorChar + repositoryName + File.separatorChar;
        final File[] repositoryDataFiles = new File(backupRepositoryPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".json");
            }
        });

        Arrays.sort(repositoryDataFiles, new BackupFileComparator());

        return Arrays.asList(repositoryDataFiles);
    }

    /**
     * Gets repository names from backup directory.
     * 
     * <p>
     * The returned repository names is the sub-directory names of the backup directory.
     * </p>
     * 
     * @return repository backup directory name
     */
    private static Set<String> getRepositoryNamesFromBackupDir() {
        final File[] repositoryBackupDirs = backupDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory();
            }
        });

        final Set<String> ret = new HashSet<String>();

        for (int i = 0; i < repositoryBackupDirs.length; i++) {
            final File file = repositoryBackupDirs[i];

            ret.add(file.getName());
        }

        return ret;
    }

    /**
     * Gets repository names.
     * 
     * @return repository names
     * @throws Exception exception
     */
    private static Set<String> getRepositoryNames() throws Exception {
        final HttpClient httpClient = new DefaultHttpClient();

        final List<NameValuePair> qparams = new ArrayList<NameValuePair>();

        qparams.add(new BasicNameValuePair("userName", userName));
        qparams.add(new BasicNameValuePair("password", password));

        final URI uri = URIUtils.createURI("http", serverAddress, -1, GET_REPOSITORY_NAMES, URLEncodedUtils.format(qparams, "UTF-8"), null);
        final HttpGet request = new HttpGet();

        request.setURI(uri);

        if (verbose) {
            System.out.println("Getting repository names[" + GET_REPOSITORY_NAMES + "]");
        }

        final HttpResponse httpResponse = httpClient.execute(request);
        final InputStream contentStream = httpResponse.getEntity().getContent();
        final String content = IOUtils.toString(contentStream).trim();

        if (verbose) {
            printResponse(content);
        }

        final JSONObject result = new JSONObject(content);
        final JSONArray repositoryNames = result.getJSONArray("repositoryNames");

        final Set<String> ret = new HashSet<String>();

        for (int i = 0; i < repositoryNames.length(); i++) {
            final String repositoryName = repositoryNames.getString(i);

            ret.add(repositoryName);

            final File dir = new File(backupDir.getPath() + File.separatorChar + repositoryName);

            if (!dir.exists() && verbose) {
                dir.mkdir();
                System.out.println(
                    "Created a directory[name=" + dir.getName() + "] under backup directory[path=" + backupDir.getPath() + "]");
            }
        }

        return ret;
    }

    /**
     * Prints the specified content as response.
     * 
     * @param content the specified content
     * @throws Exception exception 
     */
    private static void printResponse(final String content) throws Exception {
        System.out.println("Response:");

        try {
            final JSONObject response = new JSONObject(content);
            final String sc = response.optString("sc");

            if (!"200".equals(sc)) {
                System.err.println(response.toString(4));
                throw new IllegalStateException("Server response error, please check the server log for more details");
            }

            System.out.println(response.toString(4));
        } catch (final JSONException e) {
            System.out.println("The response is not a JSON [" + content + "]");
        }
    }

    /**
     * Prints help with the specified options.
     * 
     * @param options the specified options
     */
    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("latke-client", options);
    }

    /**
     * Gets options.
     * 
     * @return options
     */
    private static Options getOptions() {
        final Options ret = new Options();

        ret.addOption(
            OptionBuilder.withArgName("server").hasArg().withDescription("For server address. For example, localhost:8080").isRequired().create(
                's'));
        ret.addOption(OptionBuilder.withDescription("Create tables.").create("create_tables"));
        ret.addOption(OptionBuilder.withArgName("username").hasArg().withDescription("Username").isRequired().create('u'));
        ret.addOption(OptionBuilder.withArgName("password").hasArg().withDescription("Password").isRequired().create('p'));
        ret.addOption(OptionBuilder.withArgName("backup_dir").hasArg().withDescription("Backup directory").isRequired().create("backup_dir"));
        ret.addOption(OptionBuilder.withDescription("Backup data").create("backup"));
        ret.addOption(OptionBuilder.withDescription("Restore data").create("restore"));
        ret.addOption(
            OptionBuilder.withArgName("writable").hasArg().withDescription("Disable/Enable repository writes. For example, -w true").create(
                'w'));
        ret.addOption(
            OptionBuilder.withDescription("Prints repository names and creates directories with the repository names under" + " back_dir").create(
                "repository_names"));
        ret.addOption(OptionBuilder.withDescription("Extras verbose").create("verbose"));
        ret.addOption(OptionBuilder.withDescription("Prints help").create('h'));
        ret.addOption(OptionBuilder.withDescription("Prints this client version").create('v'));

        return ret;
    }

    /**
     * Backup file comparator by file name: ${pageNum}_${pageSize}_xxxx.json.
     * 
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 19, 2013
     */
    private static final class BackupFileComparator implements Comparator<File> {

        @Override
        public int compare(final File file1, final File file2) {
            final String name1 = file1.getName();
            final String name2 = file2.getName();

            final String pageNum1 = getBackupFileNameField(name1, "${pageNum}");
            final String pageNum2 = getBackupFileNameField(name2, "${pageNum}");

            return Integer.parseInt(pageNum1) - Integer.parseInt(pageNum2);
        }
    }

    /**
     * Private constructor.
     */
    private LatkeClient() {}
}
