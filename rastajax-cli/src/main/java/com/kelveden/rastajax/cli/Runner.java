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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Runner {

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

        final String warFile = args[0];
        final String packages = args.length > 1 ? args[1] : "com,org,net";

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
            for (FlatResourceMethod m : f.getResourceMethods()) {
                writeResourceMethod(f.getUriTemplate(), m);
            }
        }
    }

    private static void writeResourceMethod(final String resourceUriTemplate, FlatResourceMethod resourceMethod) {

        printInfo("");
        printInfo(ANSI_CYAN + resourceMethod.getRequestMethodDesignator() + " " + ANSI_GREEN + resourceUriTemplate + ANSI_RESET + " [" + resourceMethod.getResourceClass() + "]");

        printInfo("   - " + ANSI_RED + resourceMethod.getName() + ANSI_RESET + "(" + parametersToString(resourceMethod) + ")");
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
            return "   - " + header + StringUtils.join(mediaTypes.toArray(new String[mediaTypes.size()]), ", ");
        }
    }
}
