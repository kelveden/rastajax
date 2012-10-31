package com.kelveden.rastajax.representation.flat;

import java.util.Collections;
import java.util.List;

/**
 * Represents the final "representable" version of a JAX-RS resource.
 */
public final class FlatResource {

    private final String uriTemplate;
    private final List<FlatResourceMethod> resourceMethods;

    FlatResource(final String path, final List<FlatResourceMethod> resourceMethods) {
        this.uriTemplate = path;
        this.resourceMethods = Collections.unmodifiableList(resourceMethods);
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public List<FlatResourceMethod> getResourceMethods() {
        return resourceMethods;
    }


}
