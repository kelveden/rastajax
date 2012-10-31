package com.kelveden.rastajax.core.raw;

import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceClassMethodParameterTest {

    private static final String DUMMY_NAME = "somename";
    private static final Class<?> DUMMY_TYPE = String.class;
    private static final Class<? extends Annotation> DUMMY_PARAMETER_TYPE = QueryParam.class;

    @Test
    public void canInstantiate() {
        new ResourceClassMethodParameter(DUMMY_NAME, DUMMY_PARAMETER_TYPE, DUMMY_TYPE);
    }

    @Test
    public void nameIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter("myname", DUMMY_PARAMETER_TYPE, DUMMY_TYPE);

        assertThat(parameter.getName(), is("myname"));
    }

    @Test
    public void typeIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter(DUMMY_NAME, DUMMY_PARAMETER_TYPE, Integer.class);

        assertThat(parameter.getType().getName(), is("java.lang.Integer"));
    }

    @Test
    public void parameterTypeIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter(DUMMY_NAME, FormParam.class, DUMMY_TYPE);

        assertThat(parameter.getJaxRsAnnotationType().getName(), is("javax.ws.rs.FormParam"));
    }
}
