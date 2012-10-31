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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static com.kelveden.rastajax.core.raw.matchers.AnnotationTypeMatcher.*;
import static com.kelveden.rastajax.core.raw.matchers.AnnotationValueMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JaxRsAnnotationScraperTest {

    private DynamicClassCompiler compiler = null;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        compiler = new DynamicClassCompiler(tempFolder.newFolder());
    }

    @Test
    public void classLevelAnnotationIsInheritedFromSuperClass() {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"mypath\")" +
                        "public class MySuperClass { }";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass { }";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("mypath"),
                        annotationTypeIs(Path.class))));
    }

    @Test
    public void classLevelAnnotationIsInheritedFromInterface() {

        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"mypath\")" +
                        "public interface MyInterface { }";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass implements MyInterface { }";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("mypath"),
                        annotationTypeIs(Path.class))));
    }

    @Test
    public void classLevelAnnotationFromSuperClassTakesPrecedenceOverAnnotationFromInterface() {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"mypathfromsuperclass\")" +
                        "public class MySuperClass { }";

        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"mypathfrominterface\")" +
                        "public interface MyInterface { }";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass implements MyInterface { }";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, superClassSource, resourceClassSource);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("mypathfromsuperclass"),
                        annotationTypeIs(Path.class))));
    }

    @Test
    public void methodLevelAnnotationIsInheritedFromSuperClass() throws NoSuchMethodException {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "@Produces({\"application/json\"}) public Object doSomething() { return null; }" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass {" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething");

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs(arrayContaining("application/json")),
                        annotationTypeIs(Produces.class))));
    }

    @Test
    public void methodLevelAnnotationIsInheritedFromInterface() throws NoSuchMethodException {

        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "@Produces({\"application/json\"}) Object doSomething();" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass implements MyInterface {" +
                        "@Override " +
                        "public Object doSomething() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething");

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs(arrayContaining("application/json")),
                        annotationTypeIs(Produces.class))));
    }

    @Test
    public void methodLevelAnnotationFromSuperClassTakesPrecedenceOverAnnotationFromInterface() throws NoSuchMethodException {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "@Produces({\"application/json\"}) public Object doSomething() { return null; }" +
                        "}";

        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "@Produces({\"application/xml\"}) Object doSomething();" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass implements MyInterface {" +
                        "@Override " +
                        "public Object doSomething() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething");

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs(arrayContaining("application/json")),
                        annotationTypeIs(Produces.class))));
    }

    @Test
    public void parameterLevelAnnotationIsInheritedFromSuperClass() throws NoSuchMethodException {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "public Object doSomething(@QueryParam(\"myparam\") String param) { return null; }" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass {" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething", String.class);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method, 0);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("myparam"),
                        annotationTypeIs(QueryParam.class))));
    }

    @Test
    public void parameterLevelAnnotationIsInheritedFromInterface() throws NoSuchMethodException {

        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "Object doSomething(@QueryParam(\"myparam\") String param);" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass implements MyInterface {" +
                        "@Override " +
                        "public Object doSomething(String param) { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething", String.class);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method, 0);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("myparam"),
                        annotationTypeIs(QueryParam.class))));
    }

    @Test
    public void parameterLevelAnnotationFromSuperClassTakesPrecedenceOverAnnotationFromInterface() throws NoSuchMethodException {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "public Object doSomething(@QueryParam(\"superclassparam\") String param) { return null; }" +
                        "}";

        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "Object doSomething(@QueryParam(\"interfaceparam\") String param);" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass implements MyInterface {" +
                        "@Override " +
                        "public Object doSomething(String param) { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething", String.class);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method, 0);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("superclassparam"),
                        annotationTypeIs(QueryParam.class))));
    }

    @Test
    public void classLevelAnnotationFromSuperClassIsIgnoredIfResourceClassHasClassLevelAnnotationOfItsOwn() {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "@Produces(\"application/json\") @Path(\"superclasspath\")" +
                        "public class MySuperClass { }";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"classpath\")" +
                        "public class MyResourceClass extends MySuperClass { }";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("classpath"),
                        annotationTypeIs(Path.class))));
    }

    @Test
    public void classLevelAnnotationFromInterfaceIsIgnoredIfResourceClassHasClassLevelAnnotationOfItsOwn() {

        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "@Produces(\"application/json\") @Path(\"superclasspath\")" +
                        "public interface MyInterface { }";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "@Path(\"classpath\")" +
                        "public class MyResourceClass implements MyInterface { }";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("classpath"),
                        annotationTypeIs(Path.class))));
    }

    @Test
    public void methodLevelAnnotationFromSuperClassIsIgnoredIfResourceClassHasMethodLevelAnnotationOfItsOwn() throws NoSuchMethodException {

        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "@GET @Produces({\"application/json\"}) public Object doSomething() { return null; }" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass {" +
                        "@Override " +
                        "@Produces({\"application/xml\"}) public Object doSomething() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething");

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs(arrayContaining("application/xml")),
                        annotationTypeIs(Produces.class))));
    }

    @Test
    public void methodLevelAnnotationFromInterfaceIsIgnoredIfResourceClassHasMethodLevelAnnotationOfItsOwn() throws NoSuchMethodException {

        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "@GET @Produces({\"application/json\"}) Object doSomething();" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass implements MyInterface {" +
                        "@Override " +
                        "@Produces({\"application/xml\"}) public Object doSomething() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething");

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs(arrayContaining("application/xml")),
                        annotationTypeIs(Produces.class))));
    }

    @Test
    public void parameterLevelAnnotationFromSuperClassIsIgnoredIfResourceClassHasParameterLevelAnnotationOfItsOwn() throws NoSuchMethodException {


        // Given
        final String superClassSource =
                "import javax.ws.rs.*;" +
                        "public class MySuperClass {" +
                        "public Object doSomething(@DefaultValue(\"something\") @QueryParam(\"superclassparam\") String param) { return null; }" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass extends MySuperClass {" +
                        "@Override " +
                        "public Object doSomething(@QueryParam(\"classparam\") String param) { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(superClassSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething", String.class);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method, 0);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("classparam"),
                        annotationTypeIs(QueryParam.class))));
    }

    @Test
    public void parameterLevelAnnotationFromInterfaceIsIgnoredIfResourceClassHasParameterLevelAnnotationOfItsOwn() throws NoSuchMethodException {


        // Given
        final String interfaceSource =
                "import javax.ws.rs.*;" +
                        "public interface MyInterface {" +
                        "Object doSomething(@DefaultValue(\"something\") @QueryParam(\"interfaceparam\") String param);" +
                        "}";

        final String resourceClassSource =
                "import javax.ws.rs.*;" +
                        "public class MyResourceClass implements MyInterface {" +
                        "@Override " +
                        "public Object doSomething(@QueryParam(\"classparam\") String param) { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(interfaceSource, resourceClassSource);
        final Method method = compiledClass.getMethod("doSomething", String.class);

        // When
        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(compiledClass, method, 0);

        // Then
        assertThat(annotations, contains(
                Matchers.allOf(
                        annotationValueIs("classparam"),
                        annotationTypeIs(QueryParam.class))));
    }
}
