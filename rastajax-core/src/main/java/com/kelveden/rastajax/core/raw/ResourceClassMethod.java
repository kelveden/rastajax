package com.kelveden.rastajax.core.raw;

import java.util.Collections;
import java.util.List;

/**
 * Represents a public method on a {@link ResourceClass}.
 */
public abstract class ResourceClassMethod {

    private final String name;
    private final List<String> consumes;
    private final List<String> produces;
    private final List<ResourceClassMethodParameter> parameters;

    protected ResourceClassMethod(final String name, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters) {
        this.name = name;
        this.consumes = Collections.unmodifiableList(consumes);
        this.produces = Collections.unmodifiableList(produces);
        this.parameters = Collections.unmodifiableList(parameters);
    }

    public String getName() {
        return name;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public List<ResourceClassMethodParameter> getParameters() {
        return parameters;
    }
}
