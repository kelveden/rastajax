package com.kelveden.rastajax.core.raw;

import java.lang.annotation.Annotation;

/**
 * Represents a raw single parameter from a JAX-RS resource method, sub-resource method or sub-resource locator.
 */
public final class ResourceClassMethodParameter {

    private final String name;
    private final Class<?> type;
    private final Class<? extends Annotation> jaxRsAnnotationType;

    /**
     * Constructor.
     *
     * @param name
     *      The name of the parameter - i.e. how it is referred to in the JAX-RS annotation.
     * @param jaxRsAnnotationType
     *      The type of the JAX-RS annotation used to identify this parameter.
     * @param type
     *      The Java type of the parameter.
     */
    public ResourceClassMethodParameter(final String name, final Class<? extends Annotation> jaxRsAnnotationType, final Class<?> type) {
        this.name = name;
        this.type = type;
        this.jaxRsAnnotationType = jaxRsAnnotationType;
    }

    public String getName() {
        return name;
    }

    public Class<?>  getType() {
        return type;
    }

    public Class<? extends Annotation> getJaxRsAnnotationType() {
        return jaxRsAnnotationType;
    }
}
