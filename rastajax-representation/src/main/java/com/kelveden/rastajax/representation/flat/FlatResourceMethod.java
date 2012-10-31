package com.kelveden.rastajax.representation.flat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents the final "representable" version of resource method on a JAX-RS resource.
 */
public final class FlatResourceMethod {

    private final String name;
    private final String requestMethodDesignator;
    private final Map<String, List<FlatResourceMethodParameter>> parametersByType;
    private final List<String> produces;
    private final List<String> consumes;
    private final String resourceClass;

    FlatResourceMethod(final String name, final String requestMethodDesignator, final Map<String, List<FlatResourceMethodParameter>> parametersByType, final List<String> consumes, final List<String> produces, final String resourceClass) {
        this.name = name;
        this.requestMethodDesignator = requestMethodDesignator;
        this.parametersByType = Collections.unmodifiableMap(parametersByType);
        this.produces = Collections.unmodifiableList(produces);
        this.consumes = Collections.unmodifiableList(consumes);
        this.resourceClass = resourceClass;
    }

    public String getName() {
        return name;
    }

    public String getRequestMethodDesignator() {
        return requestMethodDesignator;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getProduces() {
        return produces;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getConsumes() {
        return consumes;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, List<FlatResourceMethodParameter>> getParameters() {
        return parametersByType;
    }

    public String getResourceClass() {
        return resourceClass;
    }
}
