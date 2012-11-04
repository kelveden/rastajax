/**
 * Copyright 2012 Alistair Dutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kelveden.rastajax.representation.flat;

import com.kelveden.rastajax.core.raw.*;
import com.kelveden.rastajax.core.RepresentationBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link com.kelveden.rastajax.core.RepresentationBuilder} that generates a representation where sub-resources are denormalised into a flat
 * structure.
 */
public class FlatRepresentationBuilder implements RepresentationBuilder<Set<FlatResource>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatRepresentationBuilder.class);
    private static final int UNDERLINE_LENGTH = 60;
    private static final Comparator<FlatResource> RESOURCE_COMPARATOR = new Comparator<FlatResource>() {
        @Override
        public int compare(FlatResource o1, FlatResource o2) {
            return o1.getUriTemplate().compareTo(o2.getUriTemplate());
        }
    };

    @Override
    public Set<FlatResource> buildRepresentationFor(final ResourceClass resourceClass) {

        final TreeSet<FlatResource> result = new TreeSet<FlatResource>(RESOURCE_COMPARATOR);

        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));
        LOGGER.debug("Building representation for resource with URI template {}...", resourceClass.getUriTemplate());
        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));

        if (resourceClass.isRootResource()) {

            LOGGER.debug("This resource is a root resource.");
            LOGGER.debug("Analyzing methods...");

            final Map<String, List<ResourceClassMethod>> resourceClassMethodsByPath = groupResourceClassMethodsByUriTemplate(resourceClass, " |-");

            final List<FlatResource> methodsAsResources = representResourceClassMethods(resourceClassMethodsByPath);
            result.addAll(methodsAsResources);

            LOGGER.debug("Finished analyzing methods: flattened methods to {} distinct resource(s) in representation.", methodsAsResources.size());

        } else {
            LOGGER.debug("This resource is NOT a root resource - skipping.");
        }

        return result;
    }

    @Override
    public Set<FlatResource> buildRepresentationFor(final Set<ResourceClass> resourceClasses) {

        final TreeSet<FlatResource> result = new TreeSet<FlatResource>(RESOURCE_COMPARATOR);

        for (ResourceClass rawResource : resourceClasses) {
            result.addAll(buildRepresentationFor(rawResource));
        }

        LOGGER.info("Representation completed with {} resources.", result.size());

        return result;
    }

    private MultiValuedMap groupResourceClassMethodsByUriTemplate(final ResourceClass resourceClass, final String logPrefix) {

        final MultiValuedMap<String, ResourceClassMethod> resourceClassMethodsByUriTemplate = new MultiValuedMap<String, ResourceClassMethod>();

        for (ResourceClassMethod resourceClassMethod : resourceClass.getMethods()) {

            final String uriTemplate = buildResourceMethodFullUriTemplateFrom(resourceClass, resourceClassMethod);

            LOGGER.debug("{} Found method '{}'.", logPrefix, resourceClassMethod.getName());

            if (resourceClassMethod instanceof SubResourceMethod) {

                final SubResourceMethod subResourceMethod = (SubResourceMethod) resourceClassMethod;
                LOGGER.debug("{} Method is a sub-resource method with URI template '{}' and request method designator '{}'.", logPrefix, subResourceMethod.getUriTemplate(), subResourceMethod.getRequestMethodDesignator());

                resourceClassMethodsByUriTemplate.putSingleValue(uriTemplate, resourceClassMethod);

            } else if (resourceClassMethod instanceof ResourceMethod) {

                final ResourceMethod resourceMethod = (ResourceMethod) resourceClassMethod;
                LOGGER.debug("{} Method is a resource method with request method designator '{}'.", logPrefix, resourceMethod.getRequestMethodDesignator());

                resourceClassMethodsByUriTemplate.putSingleValue(uriTemplate, resourceClassMethod);

            } else if (resourceClassMethod instanceof SubResourceLocator) {

                final SubResourceLocator subResourceLocator = (SubResourceLocator) resourceClassMethod;
                LOGGER.debug("{} Method is a sub-resource locator with URI template '{}'. Will now analyze class indicated by sub-resource locator for methods.", logPrefix, subResourceLocator.getUriTemplate());

                final ResourceClass subResource = subResourceLocator.getSubResource();

                if (subResource != null) {
                    LOGGER.debug("{} Analyzing methods on the class '{}' indicated by the sub-resource locator.", logPrefix, subResource.getRawClass());

                    final MultiValuedMap<String, ResourceClassMethod> subResourceClassMethodsByUriTemplate = getSubResourceLocatorMethodsAsResourceMethods(subResource, uriTemplate, logPrefix);
                    resourceClassMethodsByUriTemplate.mergeIn(subResourceClassMethodsByUriTemplate);

                    LOGGER.debug("{} Finished analyzing sub-resource locator with URI template {}.", logPrefix, subResourceLocator.getUriTemplate());

                } else {
                    LOGGER.debug("{} Could not find sub-resource class indicated by sub-resource locator.", logPrefix);
                }
            }
        }

        return resourceClassMethodsByUriTemplate;
    }

    private MultiValuedMap<String, ResourceClassMethod> getSubResourceLocatorMethodsAsResourceMethods(final ResourceClass subResourceLocatorSubResource, final String resourceClassUriTemplate, final String logPrefix) {

        final MultiValuedMap<String, ResourceClassMethod> subResourceUriTemplateToMethodsMap = groupResourceClassMethodsByUriTemplate(subResourceLocatorSubResource, logPrefix + "--");

        final MultiValuedMap<String, ResourceClassMethod> result = new MultiValuedMap<String, ResourceClassMethod>();

        for (Map.Entry<String, List<ResourceClassMethod>> entry : subResourceUriTemplateToMethodsMap.entrySet()) {
            final String methodUriTemplate = entry.getKey();
            final String fullUriTemplate = resourceClassUriTemplate + (methodUriTemplate == null ? "" : "/" + methodUriTemplate);

            final List<ResourceClassMethod> resourceClassMethods = entry.getValue();
            result.get(fullUriTemplate).addAll(resourceClassMethods);
        }

        return result;
    }

    private String buildResourceMethodFullUriTemplateFrom(final ResourceClass resource, final ResourceClassMethod method) {

        String fullUriTemplateRoot = "";
        if (resource.getUriTemplate() != null) {
            fullUriTemplateRoot = resource.getUriTemplate() + "/";
        }

        if (method instanceof SubResourceLocator) {
            return fullUriTemplateRoot + ((SubResourceLocator) method).getUriTemplate();

        } else if (method instanceof SubResourceMethod) {
            return fullUriTemplateRoot + ((SubResourceMethod) method).getUriTemplate();

        } else if (method instanceof ResourceMethod) {
            return resource.getUriTemplate();

        } else {
            // Will never happen as the method types will never change (unless the JAX-RS spec changes perhaps...)
            return null;
        }
    }

    private String representRequestMethodDesignator(ResourceClassMethod resourceClassMethod) {

        String requestMethodDesignator = null;
        if (resourceClassMethod instanceof SubResourceMethod) {
            requestMethodDesignator = ((SubResourceMethod) resourceClassMethod).getRequestMethodDesignator()  ;

        } else if (resourceClassMethod instanceof ResourceMethod) {
            requestMethodDesignator = ((ResourceMethod) resourceClassMethod).getRequestMethodDesignator()  ;
        }

        return requestMethodDesignator;
    }

    private Map<String, List<FlatResourceMethodParameter>> representParameters(final List<ResourceClassMethodParameter> rawMethodParameters) {

        final MultiValuedMap<String, FlatResourceMethodParameter> parameters = new MultiValuedMap<String, FlatResourceMethodParameter>();

        for (ResourceClassMethodParameter rawMethodParameter : rawMethodParameters) {
            final String parameterType = toCamelCase(rawMethodParameter.getJaxRsAnnotationType().getSimpleName());

            parameters.putSingleValue(parameterType, new FlatResourceMethodParameter(rawMethodParameter.getName(), rawMethodParameter.getType().getSimpleName()));
        }

        return parameters;
    }

    private List<FlatResource> representResourceClassMethods(final Map<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPaths) {

        final List<FlatResource> result = new ArrayList<FlatResource>();

        for (Map.Entry<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPath : subResourceMethodsGroupedByPaths.entrySet()) {
            final String uriTemplate = subResourceMethodsGroupedByPath.getKey();

            final List<FlatResourceMethod> flatResourceMethods = new ArrayList<FlatResourceMethod>();
            for (ResourceClassMethod rawMethod : subResourceMethodsGroupedByPath.getValue()) {
                flatResourceMethods.add(representResourceClassMethod(rawMethod, rawMethod.getResourceClass()));
            }

            result.add(representResource(uriTemplate, flatResourceMethods));
        }

        return result;
    }

    private FlatResourceMethod representResourceClassMethod(final ResourceClassMethod rawMethod, final ResourceClass rawResource) {

        final List<String> produces = representMediaTypeListForMethod(rawMethod.getProduces(), rawResource.getProduces());
        final List<String> consumes = representMediaTypeListForMethod(rawMethod.getConsumes(), rawResource.getConsumes());

        final Map<String, List<FlatResourceMethodParameter>> parameters = representParameters(rawMethod.getParameters());
        final String requestMethodDesignator = representRequestMethodDesignator(rawMethod);

        String resourceClassName = null;
        if (rawMethod instanceof SubResourceMethod) {
            resourceClassName = rawResource.getRawClass().getName();

        } else if (rawMethod instanceof ResourceMethod) {
            resourceClassName = ((ResourceMethod) rawMethod).getRawResourceClass().getName();

        } else {
            LOGGER.warn("Unexpected method type '{}'.", rawMethod.getClass().getName());
        }

        return new FlatResourceMethod(rawMethod.getName(), requestMethodDesignator, parameters, consumes, produces, resourceClassName);
    }

    private FlatResource representResource(final String uriTemplate, final List<FlatResourceMethod> resourceMethods) {

        final String cleanUriTemplate = cleanupUriTemplate(uriTemplate);

        LOGGER.info("Added resource with URI template '{}' to representation with {} resource methods.", cleanUriTemplate, resourceMethods.size());

        return new FlatResource(cleanUriTemplate, resourceMethods);
    }

    private List<String> representMediaTypeListForMethod(final List<String> methodLevelMediaTypes, final List<String> classLevelMediaTypes) {

        if (methodLevelMediaTypes.isEmpty()) {
            return classLevelMediaTypes;
        } else {
            return methodLevelMediaTypes;
        }
    }

    private String cleanupUriTemplate(final String uriTemplate) {
        return uriTemplate.replaceAll("/+", "/");
    }

    private String toCamelCase(final String string) {
        return string.substring(0, 1).toLowerCase(Locale.getDefault()) + string.substring(1);
    }

    private static class MultiValuedMap<K, V> extends HashMap<K, List<V>> {

        private static final long serialVersionUID = 1L;

        public void putSingleValue(final K key, final V value) {
            get(key).add(value);
        }

        public void mergeIn(final MultiValuedMap<K, V> input) {

            for (Map.Entry<K, List<V>> entry: input.entrySet()) {
                final K key = entry.getKey();
                final List<V> values = entry.getValue();

                final List<V> listToAddTo = get(key);
                listToAddTo.addAll(values);
            }
        }

        @Override
        public List<V> get(final Object key) {
            List<V> raw = super.get(key);

            if (raw == null) {
                raw = new ArrayList<V>();
                super.put((K) key, raw);
            }

            return raw;
        }
    }
}
