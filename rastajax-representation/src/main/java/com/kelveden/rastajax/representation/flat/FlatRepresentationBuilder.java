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
    public Set<FlatResource> buildRepresentationFor(ResourceClass rawResource) {

        final TreeSet<FlatResource> result = new TreeSet<FlatResource>(RESOURCE_COMPARATOR);

        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));
        LOGGER.debug("Building representation for resource with URI template {}...", rawResource.getUriTemplate());
        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));

        if (rawResource.isRootResource()) {

            LOGGER.debug("This resource is a root resource.");
            LOGGER.debug("Analyzing methods...");

            final Map<String, List<ResourceClassMethod>> methodsOnResourceByPath = groupResourceMethodsByPath(rawResource, " |-");

            final List<FlatResource> methodsAsResources = representMethodsOnResourceFrom(methodsOnResourceByPath);
            result.addAll(methodsAsResources);

            LOGGER.debug("Finished analyzing methods: flattened methods to {} distinct resource(s) in representation.", methodsAsResources.size());

        } else {
            LOGGER.debug("This resource is NOT a root resource - skipping.");
        }

        return result;
    }

    @Override
    public Set<FlatResource> buildRepresentationFor(final Set<ResourceClass> rawResources) {

        final TreeSet<FlatResource> result = new TreeSet<FlatResource>(RESOURCE_COMPARATOR);

        for (ResourceClass rawResource : rawResources) {
            result.addAll(buildRepresentationFor(rawResource));
        }

        LOGGER.info("Representation completed with {} resources.", result.size());

        return result;
    }

    private Map<String, List<ResourceClassMethod>> groupResourceMethodsByPath(final ResourceClass parentResource, final String logPrefix) {

        final Map<String, List<ResourceClassMethod>> pathToResourceMethodMap = new HashMap<String, List<ResourceClassMethod>>();

        for (ResourceClassMethod rawMethod : parentResource.getMethods()) {

            final String uriTemplate = buildResourceMethodFullUriTemplateFrom(parentResource, rawMethod);

            LOGGER.debug("{} Found method '{}'.", logPrefix, rawMethod.getName());

            if (rawMethod instanceof SubResourceMethod) {

                final SubResourceMethod subResourceMethod = (SubResourceMethod) rawMethod;
                LOGGER.debug("{} Method is a sub-resource method with URI template '{}' and request method designator '{}'.", logPrefix, subResourceMethod.getUriTemplate(), subResourceMethod.getRequestMethodDesignator());

                final List<ResourceClassMethod> rawMethodsWithPath = getRawMethodsWithPathFrom(uriTemplate, pathToResourceMethodMap);

                rawMethodsWithPath.add(rawMethod);

            } else if (rawMethod instanceof ResourceMethod) {

                final ResourceMethod resourceMethod = (ResourceMethod) rawMethod;
                LOGGER.debug("{} Method is a resource method with request method designator '{}'.", logPrefix, resourceMethod.getRequestMethodDesignator());

                final List<ResourceClassMethod> rawMethodsWithPath = getRawMethodsWithPathFrom(uriTemplate, pathToResourceMethodMap);

                rawMethodsWithPath.add(rawMethod);

            } else if (rawMethod instanceof SubResourceLocator) {

                final SubResourceLocator subResourceLocator = (SubResourceLocator) rawMethod;
                LOGGER.debug("{} Method is a sub-resource locator with URI template '{}'. Will now analyze class indicated by sub-resource locator for methods.", logPrefix, subResourceLocator.getUriTemplate());

                final ResourceClass subResource = subResourceLocator.getSubResource();

                if (subResource != null) {
                    LOGGER.debug("{} Analyzing methods on the class '{}' indicated by the sub-resource locator.", logPrefix, subResource.getRawClass());

                    final Map<String, List<ResourceClassMethod>> subResourceMethodsByPath = groupResourceMethodsByPath(subResource, logPrefix + "--");
                    for (Map.Entry<String, List<ResourceClassMethod>> entry : subResourceMethodsByPath.entrySet()) {
                        final String methodUriTemplate = entry.getKey();

                        final String fullUriTemplate = uriTemplate + (methodUriTemplate == null ? "" : "/" + methodUriTemplate);
                        final List<ResourceClassMethod> resourceClassMethods = entry.getValue();

                        if (pathToResourceMethodMap.containsKey(fullUriTemplate)) {
                            pathToResourceMethodMap.get(fullUriTemplate).addAll(resourceClassMethods);
                        } else {
                            pathToResourceMethodMap.put(fullUriTemplate, resourceClassMethods);
                        }
                    }

                    LOGGER.debug("{} Finished analyzing sub-resource locator with URI template {}.", logPrefix, subResourceLocator.getUriTemplate());

                } else {
                    LOGGER.debug("{} Could not find sub-resource class indicated by sub-resource locator.", logPrefix);
                }
            }
        }

        return pathToResourceMethodMap;
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

    private List<ResourceClassMethod> getRawMethodsWithPathFrom(final String uriTemplate, final Map<String, List<ResourceClassMethod>> pathToResourceMethodMap) {

        List<ResourceClassMethod> rawMethodsWithPath = pathToResourceMethodMap.get(uriTemplate);

        if (rawMethodsWithPath == null) {
            rawMethodsWithPath = new ArrayList<ResourceClassMethod>();
            pathToResourceMethodMap.put(uriTemplate, rawMethodsWithPath);
        }

        return rawMethodsWithPath;
    }

    private FlatResourceMethod representResourceMethodFrom(final ResourceClassMethod rawMethod, final ResourceClass rawResource) {

        final List<String> produces = representMediaTypeListFrom(rawMethod.getProduces(), rawResource.getProduces());
        final List<String> consumes = representMediaTypeListFrom(rawMethod.getConsumes(), rawResource.getConsumes());
        final Map<String, List<FlatResourceMethodParameter>> parameters = representResourceMethodParametersFrom(rawMethod.getParameters());
        final String requestMethodDesignator = representRequestMethodDesignator(rawMethod);

        String resourceClassName = null;
        if (rawMethod instanceof SubResourceMethod) {
            resourceClassName = rawResource.getRawClass().getName();
        } else if (rawMethod instanceof ResourceMethod) {
            resourceClassName = ((ResourceMethod) rawMethod).getRawResourceClass().getName();
        }

        return new FlatResourceMethod(rawMethod.getName(), requestMethodDesignator, parameters, consumes, produces, resourceClassName);
    }

    private String representRequestMethodDesignator(ResourceClassMethod rawMethod) {

        String requestMethodDesignator = null;
        if (rawMethod instanceof SubResourceMethod) {
            requestMethodDesignator = ((SubResourceMethod) rawMethod).getRequestMethodDesignator()  ;
        } else if (rawMethod instanceof ResourceMethod) {
            requestMethodDesignator = ((ResourceMethod) rawMethod).getRequestMethodDesignator()  ;
        }

        return requestMethodDesignator;
    }

    private List<String> representMediaTypeListFrom(final List<String> methodLevelMediaTypes, final List<String> classLevelMediaTypes) {

        if (methodLevelMediaTypes.isEmpty()) {
            return classLevelMediaTypes;
        } else {
            return methodLevelMediaTypes;
        }
    }

    private Map<String, List<FlatResourceMethodParameter>> representResourceMethodParametersFrom(final List<ResourceClassMethodParameter> rawMethodParameters) {

        final Map<String, List<FlatResourceMethodParameter>> parameters = new HashMap<String, List<FlatResourceMethodParameter>>();

        for (ResourceClassMethodParameter rawMethodParameter : rawMethodParameters) {

            final String parameterType = toCamelCase(rawMethodParameter.getJaxRsAnnotationType().getSimpleName());
            List<FlatResourceMethodParameter> parametersOfThisType = parameters.get(parameterType);
            if (parametersOfThisType == null) {
                parametersOfThisType = new ArrayList<FlatResourceMethodParameter>();
                parameters.put(parameterType, parametersOfThisType);
            }

            parametersOfThisType.add(new FlatResourceMethodParameter(rawMethodParameter.getName(), rawMethodParameter.getType().getSimpleName()));
        }

        return parameters;
    }

    private String toCamelCase(final String string) {
        return string.substring(0, 1).toLowerCase(Locale.getDefault()) + string.substring(1);
    }

    private List<FlatResource> representMethodsOnResourceFrom(final Map<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPaths) {

        final List<FlatResource> result = new ArrayList<FlatResource>();

        for (Map.Entry<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPath : subResourceMethodsGroupedByPaths.entrySet()) {
            final String uriTemplate = subResourceMethodsGroupedByPath.getKey();

            final List<FlatResourceMethod> flatResourceMethods = new ArrayList<FlatResourceMethod>();
            for (ResourceClassMethod rawMethod : subResourceMethodsGroupedByPath.getValue()) {
                flatResourceMethods.add(representResourceMethodFrom(rawMethod, rawMethod.getResourceClass()));
            }

            result.add(representResourceFrom(uriTemplate, flatResourceMethods));
        }

        return result;
    }

    private FlatResource representResourceFrom(final String uriTemplate, final List<FlatResourceMethod> resourceMethods) {

        final String cleanUriTemplate = cleanupUriTemplate(uriTemplate);

        LOGGER.info("Added resource with URI template '{}' to representation with {} resource methods.", cleanUriTemplate, resourceMethods.size());

        return new FlatResource(cleanUriTemplate, resourceMethods);
    }

    private String cleanupUriTemplate(final String uriTemplate) {
        return uriTemplate.replaceAll("/+", "/");
    }
}
