package com.kelveden.rastajax.core.raw;

import java.util.List;

/**
 * Represents a JAX-RS sub-resource method.
 */
public final class SubResourceMethod extends ResourceClassMethod {

    private final String uriTemplate;
    private final String requestMethodDesignator;
    private final Class<?> returnType;

    public SubResourceMethod(final String name, final String uriTemplate, final String requestMethodDesignator, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters, final Class<?> returnType) {
        super(name, consumes, produces, parameters);

        this.uriTemplate = uriTemplate;
        this.requestMethodDesignator = requestMethodDesignator;
        this.returnType = returnType;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public String getRequestMethodDesignator() {
        return requestMethodDesignator;
    }

    public Class<?> getReturnType() {
        return returnType;
    }
}
