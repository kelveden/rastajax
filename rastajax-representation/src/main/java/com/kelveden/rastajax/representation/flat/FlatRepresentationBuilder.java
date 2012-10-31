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
        LOGGER.debug("Transforming raw resource with path {}...", rawResource.getPath());
        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));

        if (rawResource.isRootResource()) {

            LOGGER.debug("This resource is a root resource.");

            final List<FlatResourceMethod> resourceMethods = getResourceMethodsFrom(rawResource);
            LOGGER.debug("This root resource has {} resource methods of its own.", resourceMethods.size());

            if (resourceMethods.size() > 0) {
                LOGGER.debug("This root resource's resource methods have been transformed into {} resource methods in the representation.", resourceMethods.size());
                result.add(representResourceFrom(rawResource.getPath(), resourceMethods));

            } else {
                LOGGER.debug("This root resource does not have any resource methods of its own; therefore will NOT add to representation.");
            }

            final Map<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPaths = groupSubResourceMethodsByPath(rawResource);

            final List<FlatResource> subResourcesAsResources = representResourceMethodsFrom(subResourceMethodsGroupedByPaths, rawResource);
            result.addAll(subResourcesAsResources);

            LOGGER.debug("Added sub-resources as resources with {} distinct URI templates.", subResourcesAsResources.size());

        } else {
            LOGGER.debug("This resource is NOT a root resource - skipping.");
        }

        return result;
    }

    @Override
    public Set<FlatResource> buildRepresentationFor(final Set<ResourceClass> rawResources) {

        final TreeSet<FlatResource> result = new TreeSet<FlatResource>(RESOURCE_COMPARATOR);

        LOGGER.info(StringUtils.repeat("=", UNDERLINE_LENGTH));
        LOGGER.info("Creating final representation...");
        LOGGER.info(StringUtils.repeat("=", UNDERLINE_LENGTH));

        for (ResourceClass rawResource : rawResources) {
            result.addAll(buildRepresentationFor(rawResource));
        }

        LOGGER.info("Representation completed as {} distinct resources.", result.size());

        return result;
    }

    private Map<String, List<ResourceClassMethod>> groupSubResourceMethodsByPath(final ResourceClass parentResource) {

        final Map<String, List<ResourceClassMethod>> pathToResourceMethodMap = new HashMap<String, List<ResourceClassMethod>>();

        for (ResourceClassMethod rawMethod : parentResource.getMethods()) {

            final String uriTemplate = buildResourceMethodFullPathFrom(parentResource, rawMethod);

            if (rawMethod instanceof SubResourceMethod) {
                final SubResourceMethod subResourceMethod = (SubResourceMethod) rawMethod;
                LOGGER.debug("Found sub-resource method with URI template '{}' and request method designator '{}'.", subResourceMethod.getUriTemplate(), subResourceMethod.getRequestMethodDesignator());

                final List<ResourceClassMethod> rawMethodsWithPath = getRawMethodsWithPathFrom(uriTemplate, pathToResourceMethodMap);

                rawMethodsWithPath.add(rawMethod);

            } else if (rawMethod instanceof SubResourceLocator) {
                final SubResourceLocator subResourceLocator = (SubResourceLocator) rawMethod;

                final List<ResourceClassMethod> rawMethodsWithPath = getRawMethodsWithPathFrom(uriTemplate, pathToResourceMethodMap);

                final ResourceClass subResource = subResourceLocator.getSubResource();

                if (subResource != null) {
                    final List<ResourceClassMethod> subResourceMethods = subResource.getMethods();

                    LOGGER.debug("Found sub-resource locator with URI template '{}' and {} sub-resource methods.", subResourceLocator.getUriTemplate(), subResourceMethods.size());

                    for (ResourceClassMethod subResourceMethod : subResourceMethods) {
                        LOGGER.debug("Found sub-resource method '{}' with request method designator '{}'.", subResourceMethod.getName(), ((ResourceMethod) subResourceMethod).getRequestMethodDesignator());

                        rawMethodsWithPath.add(subResourceMethod);
                    }

                    LOGGER.debug("This sub-resource locator has been transformed into {} resource methods on a resource with URI template '{}'.", subResourceMethods.size(), uriTemplate);

                } else {
                    LOGGER.debug("Could not find resource class corresponding to the return type from the sub-resource locator with URI template '{}'.", uriTemplate);
                }
            }
        }

        return pathToResourceMethodMap;
    }

    private String buildResourceMethodFullPathFrom(final ResourceClass resource, final ResourceClassMethod method) {

        final String methodUriTemplate;
        if (method instanceof SubResourceLocator) {
            methodUriTemplate = ((SubResourceLocator) method).getUriTemplate();
        } else if (method instanceof SubResourceMethod) {
            methodUriTemplate = ((SubResourceMethod) method).getUriTemplate();
        } else {
            methodUriTemplate = null;
        }

        if (methodUriTemplate != null) {
            return resource.getPath() + "/" + methodUriTemplate;
        } else {
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

    private List<FlatResourceMethod> getResourceMethodsFrom(final ResourceClass rawResource) {

        final List<FlatResourceMethod> resourceMethods = new ArrayList<FlatResourceMethod>();

        for (ResourceClassMethod methodOnResource : rawResource.getMethods()) {
            if (methodOnResource instanceof ResourceMethod) {
                resourceMethods.add(representResourceMethodFrom((ResourceMethod) methodOnResource, rawResource));
            }
        }

        return resourceMethods;
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

    private List<FlatResource> representResourceMethodsFrom(final Map<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPaths, final ResourceClass rawResource) {

        final List<FlatResource> result = new ArrayList<FlatResource>();

        for (Map.Entry<String, List<ResourceClassMethod>> subResourceMethodsGroupedByPath : subResourceMethodsGroupedByPaths.entrySet()) {
            final String path = subResourceMethodsGroupedByPath.getKey();

            final List<FlatResourceMethod> flatResourceMethods = new ArrayList<FlatResourceMethod>();
            for (ResourceClassMethod rawMethod : subResourceMethodsGroupedByPath.getValue()) {
                flatResourceMethods.add(representResourceMethodFrom(rawMethod, rawResource));
            }

            result.add(representResourceFrom(path, flatResourceMethods));
        }

        return result;
    }

    private FlatResource representResourceFrom(final String uriTemplate, final List<FlatResourceMethod> resourceMethods) {

        final String cleanPath = cleanupPath(uriTemplate);

        LOGGER.info("Added resource with URI template '{}' to representation with {} resource methods.", cleanPath, resourceMethods.size());

        return new FlatResource(cleanPath, resourceMethods);
    }

    private String cleanupPath(final String path) {
        return path.replaceAll("/+", "/");
    }
}
