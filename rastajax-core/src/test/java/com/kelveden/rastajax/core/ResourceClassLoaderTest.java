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

import com.kelveden.rastajax.core.raw.ResourceClass;
import com.kelveden.rastajax.core.raw.ResourceMethod;
import com.kelveden.rastajax.core.raw.SubResourceLocator;
import com.kelveden.rastajax.core.raw.SubResourceMethod;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// TODO Add tests and functionality for annotated fields/properties
public class ResourceClassLoaderTest {

    private DynamicClassCompiler compiler = null;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        compiler = new DynamicClassCompiler(tempFolder.newFolder());
    }

    @Test
    public void classWithMethodDecoratedWithRequestMethodDesignatorIsResource() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() {}" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource, notNullValue());
    }

    @Test
    public void classWithMethodDecoratedWithPathIsResource() {

        // Given
        final String subResourceSource =
                "import javax.ws.rs.*;" +
                        "public class SubResourceClass {" +
                        "@GET public Object doSomething() { return null; }" +
                        "}";

        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Path(\"something\") public SubResourceClass someMethod() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(subResourceSource, source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource, notNullValue());
    }

    @Test
    public void classWithNoMethodsIsNotAResource() {

        // Given
        final String source =
                "public class someClass {" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource, nullValue());
    }

    @Test
    public void classWithNoAnnotatedMethodsIsNotAResource() {

        // Given
        final String source =
                "public class someClass {" +
                        "public void someMethod() {  }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource, nullValue());
    }

    @Test
    public void resourceClassPathIsLoaded() {

        final String PATH = "mypath";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "@Path(\"" + PATH + "\")" +
                        "public class someClass {" +
                        "@GET public void someMethod() {}" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getUriTemplate(), is(PATH));
    }

    @Test
    public void resourceClassPathIsNullIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() {}" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getUriTemplate(), nullValue());
    }

    @Test
    public void resourceClassProducesIsLoaded() {

        final String PRODUCES1 = "my/mediatype1";
        final String PRODUCES2 = "my/mediatype2";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "@Produces({\"" + PRODUCES1 + "\", \"" + PRODUCES2 + "\"}) " +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getProduces(), contains(PRODUCES1, PRODUCES2));
    }

    @Test
    public void resourceClassProducesIsEmptyIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getProduces(), empty());
    }

    @Test
    public void resourceClassConsumesIsLoaded() {

        final String CONSUMES1 = "my/mediatype1";
        final String CONSUMES2 = "my/mediatype2";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "@Consumes({\"" + CONSUMES1 + "\", \"" + CONSUMES2 + "\"}) " +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getConsumes(), contains(CONSUMES1, CONSUMES2));
    }

    @Test
    public void resourceClassConsumesIsEmptyIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getConsumes(), empty());
    }

    @Test
    public void methodsFromResourceClassAreLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod1() {}" +
                        "@PUT public void someMethod2() {}" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods(), hasSize(2));
    }

    @Test
    public void methodPathIsLoaded() {

        final String PATH = "mypath";

        // Given
        final String subResourceSource =
                "import javax.ws.rs.*;" +
                        "public class SubResourceClass {" +
                        "@GET public Object doSomething() { return null; }" +
                        "}";

        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Path(\"" + PATH + "\") public SubResourceClass someMethod() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(subResourceSource, source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(((SubResourceLocator) resource.getMethods().get(0)).getUriTemplate(), is(PATH));
    }

    @Test
    public void methodPathIsNullIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0), is(instanceOf(ResourceMethod.class)));
    }

    @Test
    public void methodRequestMethodDesignatorIsLoaded() {

        final String REQUEST_METHOD_DESIGNATOR = "PUT";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@" + REQUEST_METHOD_DESIGNATOR + " public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(((ResourceMethod) resource.getMethods().get(0)).getRequestMethodDesignator(), is(REQUEST_METHOD_DESIGNATOR));
    }

    @Test
    public void methodRequestMethodDesignatorIsNullIfMissing() {

        // Given
        final String subResourceSource =
                "import javax.ws.rs.*;" +
                        "public class SubResourceClass {" +
                        "@GET public Object doSomething() { return null; }" +
                        "}";

        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Path(\"somepath\") public SubResourceClass someMethod() { return null; }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(subResourceSource, source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0), is(instanceOf(SubResourceLocator.class)));
    }

    @Test
    public void methodProducesIsLoaded() {

        final String PRODUCES1 = "my/mediatype1";
        final String PRODUCES2 = "my/mediatype2";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Produces({\"" + PRODUCES1 + "\", \"" + PRODUCES2 + "\"}) " +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getProduces(), contains(PRODUCES1, PRODUCES2));
    }

    @Test
    public void methodProducesIsEmptyIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getProduces(), empty());
    }

    @Test
    public void methodConsumesIsLoaded() {

        final String CONSUMES1 = "my/mediatype1";
        final String CONSUMES2 = "my/mediatype2";

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Consumes({\"" + CONSUMES1 + "\", \"" + CONSUMES2 + "\"}) " +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getConsumes(), contains(CONSUMES1, CONSUMES2));
    }

    @Test
    public void methodConsumesIsEmptyIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getConsumes(), empty());
    }

    @Test
    public void methodParametersAreLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@QueryParam(\"myparam1\") final String param1, @FormParam(\"myparam2\") final String param2) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters(), hasSize(2));
    }

    @Test
    public void methodParametersAreEmptyIfMissing() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters(), empty());
    }

    @Test
    public void methodParameterIsIgnoredIfNotAnnotated() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(final String param1) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters(), empty());
    }

    @Test
    public void methodParameterQueryParamIsLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@QueryParam(\"myparam\") final String param) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getName(), is("myparam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getJaxRsAnnotationType().getSimpleName(), is("QueryParam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getType().getSimpleName(), is("String"));
    }

    @Test
    public void methodParameterFormParamIsLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@FormParam(\"myparam\") final String param) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getName(), is("myparam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getJaxRsAnnotationType().getSimpleName(), is("FormParam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getType().getSimpleName(), is("String"));
    }

    @Test
    public void methodParameterHeaderParamIsLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@HeaderParam(\"myparam\") final String param) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getName(), is("myparam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getJaxRsAnnotationType().getSimpleName(), is("HeaderParam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getType().getSimpleName(), is("String"));
    }

    @Test
    public void methodParameterMatrixParamIsLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@MatrixParam(\"myparam\") final String param) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getName(), is("myparam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getJaxRsAnnotationType().getSimpleName(), is("MatrixParam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getType().getSimpleName(), is("String"));
    }

    @Test
    public void methodParameterPathParamIsLoaded() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod(@PathParam(\"myparam\") final String param) { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getName(), is("myparam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getJaxRsAnnotationType().getSimpleName(), is("PathParam"));
        MatcherAssert.assertThat(resource.getMethods().get(0).getParameters().get(0).getType().getSimpleName(), is("String"));
    }

    @Test
    public void resourceMethodWithRequestMethodDesignatorButNoPathIsResourceMethod() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0), is(instanceOf(ResourceMethod.class)));
    }

    @Test
    public void resourceMethodWithRequestMethodDesignatorAndPathIsSubResourceMethod() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@GET @Path(\"some/path\") public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0), is(instanceOf(SubResourceMethod.class)));
    }

    @Test
    public void methodWithNoRequestMethodDesignatorAndPathIsSubResourceLocator() {

        // Given
        final String source =
                "import javax.ws.rs.*;" +
                        "public class someClass {" +
                        "@Path(\"some/path\") public void someMethod() { }" +
                        "}";

        final Class<?> compiledClass = compiler.compileFromSource(source);

        // When
        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resource = loader.loadResourceClassFrom(compiledClass);

        // Then
        MatcherAssert.assertThat(resource.getMethods().get(0), is(instanceOf(SubResourceLocator.class)));
    }
}
