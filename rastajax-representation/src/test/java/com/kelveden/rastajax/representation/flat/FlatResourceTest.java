package com.kelveden.rastajax.representation.flat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class FlatResourceTest {

    private static final String DUMMY_PATH = "some/path";
    private static final List<FlatResourceMethod> DUMMY_RESOURCE_METHODS = new ArrayList<FlatResourceMethod>();
    private static final String DUMMY_RESOURCE_CLASS = "SomeClass";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new FlatResource(DUMMY_PATH, DUMMY_RESOURCE_METHODS);
    }

    @Test
    public void pathIsLoaded() {

        final FlatResource resource = new FlatResource("my/path", DUMMY_RESOURCE_METHODS);

        assertThat(resource.getUriTemplate(), is("my/path"));
    }

    @Test
    public void resourceMethodsIsLoaded() {

        final List<FlatResourceMethod> resourceMethods = Arrays.asList(dummyResourceMethod(), dummyResourceMethod());

        final FlatResource resource = new FlatResource(DUMMY_PATH, resourceMethods);

        assertThat(resource.getResourceMethods(), hasSize(2));
    }

    @Test
    public void resourceMethodsIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final FlatResource resource = new FlatResource(DUMMY_PATH, DUMMY_RESOURCE_METHODS);

        resource.getResourceMethods().clear();
    }

    private FlatResourceMethod dummyResourceMethod() {
        return new FlatResourceMethod("somename", "somerequestmethoddesignator", new HashMap<String, List<FlatResourceMethodParameter>>(), new ArrayList<String>(), new ArrayList<String>(), DUMMY_RESOURCE_CLASS);
    }
}
