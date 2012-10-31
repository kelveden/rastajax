package com.kelveden.rastajax.core.raw;

import java.util.List;

/**
 * Represents a JAX-RS resource method.
 */
public final class ResourceMethod extends ResourceClassMethod {

    private final String requestMethodDesignator;
    private final Class<?> returnType;
    private final Class<?> rawResourceClass;

    public ResourceMethod(final String name, final String requestMethodDesignator, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters, final Class<?> returnType, final Class<?> rawResourceClass) {
        super(name, consumes, produces, parameters);

        this.requestMethodDesignator = requestMethodDesignator;
        this.returnType = returnType;
        this.rawResourceClass = rawResourceClass;
    }

    public String getRequestMethodDesignator() {
        return requestMethodDesignator;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?> getRawResourceClass() {
        return rawResourceClass;
    }
}
