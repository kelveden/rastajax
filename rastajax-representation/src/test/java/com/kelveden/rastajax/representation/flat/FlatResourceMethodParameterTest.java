package com.kelveden.rastajax.representation.flat;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlatResourceMethodParameterTest {

    private static final String DUMMY_NAME = "somename";
    private static final String DUMMY_TYPE = "sometype";

    @Test
    public void canInstantiate() {
        new FlatResourceMethodParameter(DUMMY_NAME, DUMMY_TYPE);
    }

    @Test
    public void nameIsLoaded() {

        final FlatResourceMethodParameter parameter = new FlatResourceMethodParameter("myname", DUMMY_TYPE);

        assertThat(parameter.getName(), is("myname"));
    }

    @Test
    public void typeIsLoaded() {

        final FlatResourceMethodParameter parameter = new FlatResourceMethodParameter(DUMMY_NAME, "mytype");

        assertThat(parameter.getType(), is("mytype"));
    }
}
