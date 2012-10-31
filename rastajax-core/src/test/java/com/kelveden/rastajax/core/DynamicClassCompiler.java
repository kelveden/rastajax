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
package com.kelveden.rastajax.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.*;

/**
 * Provides functionality for dynamically creating classes on the fly from provided source.
 */
public class DynamicClassCompiler {

    private Pattern CLASS_NAME_PATTERN = Pattern.compile("(class|interface) ([^ ]+?) ");
    private Pattern PACKAGE_NAME_PATTERN = Pattern.compile("package (.+?);");
    private final File workingFolder;
    private final ClassLoader classLoader;

    /**
     * Constructor.
     *
     * @param workingFolder
     *      The folder which will be used to create source files and compiled class files.
     */
    public DynamicClassCompiler(final File workingFolder) {
        this.workingFolder = workingFolder;

        try {
            final URL classDirectoryURL = new URL("file://" + workingFolder.getAbsolutePath() + "/");
            classLoader = new URLClassLoader(new URL[] { classDirectoryURL });

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a handle to the {@link ClassLoader} that will provide access to the classes compiled by this {@link DynamicClassCompiler}.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Compiles the source code for the specified class(es) and returns the compiled {@link Class} for the last class. The source code is compiled in order so
     * make sure that source for dependencies is passed before the source for the class that is dependent on them.
     *
     * @param classSourceCodes
     *      The source code for the class(es).
     * @return
     *      The {@link Class} compiled from the last source code.
     */
    public Class<?> compileFromSource(final String... classSourceCodes) {

        String lastClassName = null;
        String lastPackageName = null;
        final Set<String> sourceFilePaths = new HashSet<String>();

        for (String classSourceCode : classSourceCodes) {
            final String className = getClassNameFromSource(classSourceCode);
            final String packageName = getPackageNameFromSource(classSourceCode);

            lastClassName = className;
            lastPackageName = packageName;

            final File sourceFile = createSourceFile(classSourceCode, packageName, className);

            sourceFilePaths.add(sourceFile.getAbsolutePath());
        }

        compileFromFiles(sourceFilePaths);

        try {
            return Class.forName(createFullClassName(lastPackageName, lastClassName), true, classLoader);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private File createSourceFile(final String sourceCode, final String packageName, final String className) {

        File packageFolder = workingFolder;
        if (!StringUtils.isEmpty(packageName)) {
             packageFolder = new File(workingFolder, packageName.replace(".", File.separator));
        }

        final File sourceFile = new File(packageFolder, className + ".java");

        try {
            FileUtils.write(sourceFile, sourceCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sourceFile;
    }

    private String createFullClassName(final String packageName, final String className) {
        if (StringUtils.isEmpty(packageName)) {
            return className;
        } else {
            return packageName + "." + className;
        }
    }

    private String getClassNameFromSource(final String classSourceCode) {

        final Matcher matcher = CLASS_NAME_PATTERN.matcher(classSourceCode);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            throw new RuntimeException("Class name could not be parsed from source.");
        }
    }

    private String getPackageNameFromSource(final String classSourceCode) {

        final Matcher matcher = PACKAGE_NAME_PATTERN.matcher(classSourceCode);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private void compileFromFiles(final Set<String> sourceFilePaths) {

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        try {
            fileManager = compiler.getStandardFileManager(null, null, null);

            final Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjectsFromStrings(sourceFilePaths);

            if (!compiler.getTask(null, null, null, null, null, fileObjects).call()) {
                throw new RuntimeException("Failed to compile class.");
            }
        } finally {
            IOUtils.closeQuietly(fileManager);
        }
    }
}
