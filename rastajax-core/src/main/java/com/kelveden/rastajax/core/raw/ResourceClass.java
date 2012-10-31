package com.kelveden.rastajax.core.raw;

import java.util.Collections;
import java.util.List;

/**
 * Represents the raw data from a JAX-RS resource.
 */
public final class ResourceClass {

    private final Class<?> rawClass;
    private final String path;
    private final List<ResourceClassMethod> methods;
    private final List<String> consumes;
    private final List<String> produces;

    public ResourceClass(final Class<?> rawClass, final String path, final List<ResourceClassMethod> methodsOnResource, final List<String> consumes, final List<String> produces) {
        this.rawClass = rawClass;
        this.path = path;
        this.methods = Collections.unmodifiableList(methodsOnResource);
        this.consumes = Collections.unmodifiableList(consumes);
        this.produces = Collections.unmodifiableList(produces);
    }

    public String getPath() {
        return path;
    }

    public List<ResourceClassMethod> getMethods() {
        return methods;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public boolean isRootResource() {
        return (path != null) && (methods.size() > 0) ;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }
}
