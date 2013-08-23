/**
 * Copyright 2012 Alistair Dutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kelveden.rastajax.cli;

import com.kelveden.rastajax.core.ClassLoaderRootResourceScanner;
import com.kelveden.rastajax.core.RestDescriber;
import com.kelveden.rastajax.representation.flat.FlatRepresentationBuilder;
import com.kelveden.rastajax.representation.flat.FlatResource;
import com.kelveden.rastajax.representation.flat.FlatResourceMethod;
import com.kelveden.rastajax.representation.flat.FlatResourceMethodParameter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) throws IOException {

        final File warFile = args.length > 0 ? new File(args[0]) : findWar();
        final String packages = args.length > 1 ? args[1] : "com,org,net";

        LOGGER.info("Loading war from " + warFile.getAbsolutePath());

        final File tempFolder = new File(FileUtils.getTempDirectory(), "rastajax-explode");
        FileUtils.forceMkdir(tempFolder);
        FileUtils.cleanDirectory(tempFolder);


        try {
            ZipFile zipFile = new ZipFile(warFile);
            zipFile.extractAll(tempFolder.getAbsolutePath());

        } catch (ZipException e) {
            e.printStackTrace();
        }

        final File classesFolder = new File(tempFolder, "WEB-INF/classes");
        final File libFolder = new File(tempFolder, "WEB-INF/lib");

        final List<URL> urls = new ArrayList<URL>();
        urls.add(classesFolder.toURI().toURL());

        for (File file : FileUtils.listFiles(libFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            urls.add(file.toURI().toURL());
        };

        final ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[] {}), Runner.class.getClassLoader());

        final ClassLoaderRootResourceScanner scanner = new ClassLoaderRootResourceScanner(
                classLoader,
                packages.split(",")).allowInterfaceInheritance();

        final Set<FlatResource> representation = RestDescriber.describeApplication(
                scanner.scan(),
                new FlatRepresentationBuilder());

        for (FlatResource f : representation) {
            printInfo("");
            printInfo(ANSI_GREEN + f.getUriTemplate() + ANSI_RESET);
            for (FlatResourceMethod m : f.getResourceMethods()) {
                writeResourceMethod(m);
            }
        }
    }

    private static File findWar() {
        final Collection<File> wars = FileUtils.listFiles(new File(System.getProperty("user.dir")), new String[] { "war" }, true);

        LOGGER.info("Found the following wars: " + wars.toString());

        if (wars.size() > 0) {
            return wars.iterator().next();
        } else {
            return null;
        }
    }

    private static void writeResourceMethod(FlatResourceMethod resourceMethod) {

        printInfo("    " + ANSI_CYAN + resourceMethod.getRequestMethodDesignator() + ANSI_RESET + " [" + resourceMethod.getResourceClass() + "]");

        printInfo("       - " + ANSI_RED + resourceMethod.getName() + ANSI_RESET + "(" + parametersToString(resourceMethod) + ")");
        printInfo(mediaTypesToString(ANSI_CYAN + "Produces: " + ANSI_RESET, resourceMethod.getProduces()));
        printInfo(mediaTypesToString(ANSI_BLUE + "Consumes: " + ANSI_RESET, resourceMethod.getConsumes()));
    }

    private static void printInfo(final String info) {
        if (info != null) {
            System.out.println(info);
        }
    }

    private static String parametersToString(final FlatResourceMethod method) {

        final List<String> parameterNames = new ArrayList<String>();

        for (Map.Entry<String, List<FlatResourceMethodParameter>> parameterEntry : method.getParameters().entrySet()) {
            for (FlatResourceMethodParameter parameter : parameterEntry.getValue()) {
                parameterNames.add(ANSI_CYAN + parameter.getType() + ANSI_RESET + " " + parameter.getName());
            }
        }

        return StringUtils.join(parameterNames.toArray(new String[parameterNames.size()]), ", ");
    }

    private static String mediaTypesToString(final String header, final List<String> mediaTypes) {

        if (mediaTypes.size() == 0) {
            return null;
        } else {
            return "       - " + header + StringUtils.join(mediaTypes.toArray(new String[mediaTypes.size()]), ", ");
        }
    }
}
