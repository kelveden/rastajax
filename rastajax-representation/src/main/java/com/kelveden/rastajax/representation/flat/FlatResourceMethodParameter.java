package com.kelveden.rastajax.representation.flat;

/**
 * Represents the final "representable" version of a parameter on a resource method of a JAX-RS resource.
 */
public final class FlatResourceMethodParameter {

    private final String name;
    private final String type;

    FlatResourceMethodParameter(final String name, final String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
