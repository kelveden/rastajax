package com.kelveden.rastajax.core.raw;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ResourceClassTest {

    private static final String NO_PATH = null;
    private static final String DUMMY_PATH = "somepath";

    private static final List<ResourceClassMethod> EMPTY_METHODs = new ArrayList<ResourceClassMethod>();
    private static final List<ResourceClassMethod> DUMMY_METHODs = EMPTY_METHODs;

    private static final List<String> DUMMY_PRODUCES = new ArrayList<String>();
    private static final List<String> DUMMY_CONSUMES = new ArrayList<String>();
    
    private static final Class<?> DUMMY_RESOURCE_CLASS = String.class;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);
    }

    @Test
    public void pathIsLoaded() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "mypath", DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.getPath(), is("mypath"));
    }

    @Test
    public void methodsAreLoaded() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, Arrays.asList(dummyMethod(), dummyMethod()), DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.getMethods(), hasSize(2));
    }

    @Test
    public void methodsIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        resource.getMethods().add(dummyMethod());
    }

    @Test
    public void producesIsLoaded() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, DUMMY_CONSUMES, Arrays.asList("produces1", "produces2"));

        assertThat(resource.getProduces(), contains("produces1", "produces2"));
    }

    @Test
    public void producesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        resource.getProduces().add("produces");
    }

    @Test
    public void consumesIsLoaded() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, Arrays.asList("consumes1", "consumes2"), DUMMY_PRODUCES);

        assertThat(resource.getConsumes(), contains("consumes1", "consumes2"));
    }

    @Test
    public void consumesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, DUMMY_PATH, DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        resource.getConsumes().add("consumes");
    }

    @Test
    public void isRootResourceIfPathAndAtLeastOneResourceMethod() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", Arrays.asList(dummyMethod()), DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.isRootResource(), is(true));
    }

    @Test
    public void isNotRootResourceIfNoPath() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, NO_PATH, Arrays.asList(dummyMethod()), DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.isRootResource(), is(false));
    }

    @Test
    public void isNotRootResourceIfNoResourceMethods() {

        final ResourceClass resource = new ResourceClass(DUMMY_RESOURCE_CLASS, "my/path", EMPTY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.isRootResource(), is(false));
    }

    @Test
    public void resourceClassIsLoaded() {

        final ResourceClass resource = new ResourceClass(Integer.class, "mypath", DUMMY_METHODs, DUMMY_CONSUMES, DUMMY_PRODUCES);

        assertThat(resource.getRawClass().getName(), is("java.lang.Integer"));
    }

    private ResourceClassMethod dummyMethod() {
        return new ResourceMethod("somename", "somerequestmethodesignator", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<ResourceClassMethodParameter>(), null, DUMMY_RESOURCE_CLASS);
    }
}

