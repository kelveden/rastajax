package com.kelveden.rastajax.core.raw;

import java.util.List;

/**
 * Represents a JAX-RS sub-resource locator.
 */
public class SubResourceLocator extends ResourceClassMethod {

    private final String uriTemplate;
    private final ResourceClass subResource;

    public SubResourceLocator(final String name, final String uriTemplate, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters, final ResourceClass subResource) {
        super(name, consumes, produces, parameters);

        this.uriTemplate = uriTemplate;
        this.subResource = subResource;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public ResourceClass getSubResource() {
        return subResource;
    }
}
