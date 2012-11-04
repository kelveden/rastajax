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
package com.kelveden.rastajax.representation.flat;

import com.kelveden.rastajax.core.raw.*;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlatRepresentationBuilderTest {

    private static final List<String> EMPTY_PRODUCES = new ArrayList<String>();
    private static final List<String> DUMMY_PRODUCES = EMPTY_PRODUCES;

    private static final List<String> EMPTY_CONSUMES = new ArrayList<String>();
    private static final List<String> DUMMY_CONSUMES = EMPTY_CONSUMES;

    private static final List<ResourceClassMethodParameter> EMPTY_PARAMETERS = new ArrayList<ResourceClassMethodParameter>();
    private static final List<ResourceClassMethodParameter> DUMMY_PARAMETERS = EMPTY_PARAMETERS;

    private static final String NO_PATH = null;
    private static final String DUMMY_PATH = "some/path";

    private static final String DUMMY_REQUEST_METHOD_DESIGNATOR = "PUT";
    private static final String DUMMY_METHOD_NAME = "somemethod";

    private static final Class<?> NO_RETURN_TYPE = null;
    private static final Class<?> DUMMY_RESOURCE_CLASS = String.class;
    private static final ResourceClassMethod DUMMY_METHOD = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_PARAMETERS, NO_RETURN_TYPE);

    @Test
    public void canInstantiate() {
        new FlatRepresentationBuilder();
    }

    @Test
    public void rawResourceWithNoMethodsIsNotIncludedInRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", new ArrayList<ResourceClassMethod>(), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(0));
    }

    @Test
    public void rawResourceWithNoPathIsNotIncludedAsAResourceInRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, NO_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(0));
    }

    @Test
    public void extraneousSlashesFromPathsAreStrippedFromClass() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "///my//path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getUriTemplate(), is("/my/path"));
    }

    @Test
    public void extraneousSlashesFromPathsAreStrippedFromMethods() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new SubResourceMethod(DUMMY_METHOD_NAME, "my////path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "class/path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getUriTemplate(), is("class/path/my/path"));
    }

    @Test
    public void ifMissingSlashIsAddedToFrontOfMethodPathWhenAppendingToClassPath() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new SubResourceMethod(DUMMY_METHOD_NAME, "my/path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "class/path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getUriTemplate(), is("class/path/my/path"));
    }

    @Test
    public void ifNotMissingSlashIsNotAddedToFrontOfMethodPathWhenAppendingToClassPath() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new SubResourceMethod(DUMMY_METHOD_NAME, "/my/path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "class/path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getUriTemplate(), is("class/path/my/path"));
    }

    @Test
    public void rawResourceWithResourceMethodsIsIncludedInRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(DUMMY_METHOD), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getUriTemplate(), is("my/path"));
    }

    @Test
    public void resourceMethodIsIncludedInRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod("mymethod", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods(), hasSize(1));
        assertThat(result.iterator().next().getResourceMethods().get(0).getName(), is("mymethod"));
    }

    @Test
    public void resourceMethodResourceClassIsThatOfTheResource() {

        final Class<?> expectedRawClass = Integer.class;
        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod("mymethod", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(expectedRawClass, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getResourceClass(), is("java.lang.Integer"));
    }

    @Test
    public void subResourceLocatorIsAddedToRepresentationAsResourceMethodOfNewResource() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod resourceMethod = new ResourceMethod("mymethod", "POST", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass subResource = new ResourceClass(DUMMY_RESOURCE_CLASS, NO_PATH, Arrays.asList(resourceMethod), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final ResourceClassMethod subResourceLocator = new SubResourceLocator(DUMMY_METHOD_NAME, "my/subresource/path", DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_PARAMETERS, subResource) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(subResourceLocator), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getUriTemplate(), is("my/path/my/subresource/path"));
        assertThat(result.iterator().next().getResourceMethods(), hasSize(1));
        assertThat(result.iterator().next().getResourceMethods().get(0).getName(), is("mymethod"));
    }

    @Test
    public void subResourceLocatorResourceClassIsThatOfTheSubResource() {

        final Class<?> subResourceClass = Integer.class;
        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod resourceMethod = new ResourceMethod("mymethod", "POST", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass subResource = new ResourceClass(subResourceClass, NO_PATH, Arrays.asList(resourceMethod), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final ResourceClassMethod subResourceLocator = new SubResourceLocator(DUMMY_METHOD_NAME, "my/subresource/path", DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_PARAMETERS, subResource) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(subResourceLocator), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getResourceClass(), is("java.lang.Integer"));
    }

    @Test
    public void subResourceLocatorsWithMatchingPathsAreAddedToRepresentationAsResourceMethodsOfSingleResource() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod resourceMethod1 = new ResourceMethod("mymethod1", "POST", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass subResource1 = new ResourceClass(DUMMY_RESOURCE_CLASS, NO_PATH, Arrays.asList(resourceMethod1), DUMMY_CONSUMES, DUMMY_PRODUCES);
        final ResourceClassMethod resourceMethod2 = new ResourceMethod("mymethod2", "PUT", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass subResource2 = new ResourceClass(DUMMY_RESOURCE_CLASS, NO_PATH, Arrays.asList(resourceMethod2), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final ResourceClassMethod subResourceLocator1 = new SubResourceLocator(DUMMY_METHOD_NAME, "my/subresource/path", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, subResource1) ;
        final ResourceClassMethod subResourceLocator2 = new SubResourceLocator(DUMMY_METHOD_NAME, "my/subresource/path", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, subResource2) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(subResourceLocator1, subResourceLocator2), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getUriTemplate(), is("my/path/my/subresource/path"));
        assertThat(result.iterator().next().getResourceMethods(), hasSize(2));
        assertThat(result.iterator().next().getResourceMethods().get(0).getName(), is("mymethod1"));
        assertThat(result.iterator().next().getResourceMethods().get(1).getName(), is("mymethod2"));
    }

    @Test
    public void rawResourceClassLevelConsumesTranslatesIntoMethodConsumes() {

        final String expectedConsumes = "application/json";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(DUMMY_METHOD), Arrays.asList(expectedConsumes), DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getConsumes(), contains(expectedConsumes));
    }

    @Test
    public void resourceMethodConsumesTranslatesIntoMethodConsumes() {

        final String expectedConsumes = "application/json";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, Arrays.asList(expectedConsumes), DUMMY_PRODUCES, DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getConsumes(), contains(expectedConsumes));
    }

    @Test
    public void resourceMethodConsumesTakesPrecedenceOverRawResourceClassLevelConsumesOnMethod() {

        final String expectedConsumes = "application/json";
        final String classLevelConsumes = "application/xml";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, Arrays.asList(expectedConsumes), DUMMY_PRODUCES, DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), Arrays.asList(classLevelConsumes), DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getConsumes(), contains(expectedConsumes));
    }

    @Test
    public void rawResourceClassLevelProducesTranslatesIntoMethodProduces() {

        final String expectedProduces = "application/json";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(DUMMY_METHOD), DUMMY_CONSUMES, Arrays.asList(expectedProduces));

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getProduces(), contains(expectedProduces));
    }

    @Test
    public void resourceMethodProducesTranslatesIntoMethodProduces() {

        final String expectedProduces = "application/json";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, Arrays.asList(expectedProduces), DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getProduces(), contains(expectedProduces));
    }

    @Test
    public void resourceMethodProducesTakesPrecedenceOverRawResourceClassLevelProducesOnMethod() {

        final String expectedProduces = "application/json";
        final String classLevelProduces = "application/xml";

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, Arrays.asList(expectedProduces), DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, Arrays.asList(classLevelProduces));

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getProduces(), contains(expectedProduces));
    }

    @Test
    public void rawResourceMethodParameterNamesAreAddedToRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethodParameter param1 = new ResourceClassMethodParameter("param1", QueryParam.class, String.class);
        final ResourceClassMethodParameter param2 = new ResourceClassMethodParameter("param2", QueryParam.class, String.class);

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, Arrays.asList(param1, param2), NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("queryParam").get(0).getName(), is("param1"));
        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("queryParam").get(1).getName(), is("param2"));
    }

    @Test
    public void rawResourceMethodParameterTypesAreAddedToRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethodParameter param1 = new ResourceClassMethodParameter("param1", QueryParam.class, String.class);
        final ResourceClassMethodParameter param2 = new ResourceClassMethodParameter("param2", QueryParam.class, Integer.class);

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, Arrays.asList(param1, param2), NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("queryParam").get(0).getType(), is("String"));
        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("queryParam").get(1).getType(), is("Integer"));
    }

    @Test
    public void rawResourceMethodParametersAreGroupedByTypeInRepresentation() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethodParameter param1 = new ResourceClassMethodParameter("param1", QueryParam.class, String.class);
        final ResourceClassMethodParameter param2 = new ResourceClassMethodParameter("param2", FormParam.class, String.class);
        final ResourceClassMethodParameter param3 = new ResourceClassMethodParameter("param3", FormParam.class, String.class);

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, Arrays.asList(param1, param2, param3), NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("queryParam"), hasSize(1));
        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().get("formParam"), hasSize(2));
    }

    @Test
    public void resourceMethodParametersAreEmptyIfNoneFoundOnRawResourceMethod() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new ResourceMethod(DUMMY_METHOD_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, new ArrayList<ResourceClassMethodParameter>(), NO_RETURN_TYPE);
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getParameters().size(), is(0));
    }

    @Test
    public void subResourceMethodIsAddedToRepresentationAsResourceMethodOfNewResource() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new SubResourceMethod("mymethod", "my/subresource/path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getUriTemplate(), is("my/path/my/subresource/path"));
        assertThat(result.iterator().next().getResourceMethods(), hasSize(1));
        assertThat(result.iterator().next().getResourceMethods().get(0).getName(), is("mymethod"));
    }

    @Test
    public void subResourceMethodResourceClassIsThatOfTheResourceItself() {

        final Class<?> resourceClass = Integer.class;
        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method = new SubResourceMethod("mymethod", "my/subresource/path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(resourceClass, "my/path", Arrays.asList(method), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result.iterator().next().getResourceMethods().get(0).getResourceClass(), is("java.lang.Integer"));
    }

    @Test
    public void subResourceMethodsWithMatchingPathsAreAddedToRepresentationAsResourceMethodsOfSingleResource() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClassMethod method1 = new SubResourceMethod("mymethod1", "my/subresource/path", "GET", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClassMethod method2 = new SubResourceMethod("mymethod2", "my/subresource/path", "PUT", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, NO_RETURN_TYPE) ;
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(method1, method2), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));

        final FlatResource firstResource = result.iterator().next();
        assertThat(firstResource.getUriTemplate(), is("my/path/my/subresource/path"));
        assertThat(firstResource.getResourceMethods(), hasSize(2));
        assertThat(firstResource.getResourceMethods().get(0).getName(), is("mymethod1"));
        assertThat(firstResource.getResourceMethods().get(1).getName(), is("mymethod2"));
    }

    @Test
    public void resourcesAreOrderedAlphabeticallyByPath() {

        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        final ResourceClass resource1 = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path2", Arrays.asList(DUMMY_METHOD), DUMMY_CONSUMES, DUMMY_PRODUCES);
        final ResourceClass resource2 = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path3", Arrays.asList(DUMMY_METHOD), DUMMY_CONSUMES, DUMMY_PRODUCES);
        final ResourceClass resource3 = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path1", Arrays.asList(DUMMY_METHOD), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource1, resource2, resource3)));

        final Iterator<FlatResource> resources = result.iterator();
        assertThat(resources.next().getUriTemplate(), is("my/path1"));
        assertThat(resources.next().getUriTemplate(), is("my/path2"));
        assertThat(resources.next().getUriTemplate(), is("my/path3"));
    }

    @Test
    public void subResourceMethodsOfSubResourceLocatorMethodsAreFlattened() {
        final FlatRepresentationBuilder builder = new FlatRepresentationBuilder();

        // Create sub-resource
        final ResourceClassMethod subResourceMethod = new SubResourceMethod(DUMMY_METHOD_NAME, "subresourcemethod/path", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_PARAMETERS, NO_RETURN_TYPE);
        final ResourceClass subResource = new ResourceClass(this.getClass(), NO_PATH, Arrays.asList(subResourceMethod), DUMMY_CONSUMES, DUMMY_PRODUCES);

        // Point new sub-resource locator to it
        final ResourceClassMethod subResourceLocator = new SubResourceLocator(DUMMY_METHOD_NAME, "my/subresource/path", DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_PARAMETERS, subResource) ;

        // Now create resource class referencing the sub-resource locator
        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(subResourceLocator), DUMMY_CONSUMES, DUMMY_PRODUCES);

        final Set<FlatResource> result = builder.buildRepresentationFor(new HashSet<ResourceClass>(Arrays.asList(resource)));

        assertThat(result, hasSize(1));

        final FlatResource flatResource = result.iterator().next();
        assertThat(flatResource.getUriTemplate(), is("my/path/my/subresource/path/subresourcemethod/path"));
        assertThat(flatResource.getResourceMethods().get(0).getResourceClass(), is(this.getClass().getName()));
    }
}
