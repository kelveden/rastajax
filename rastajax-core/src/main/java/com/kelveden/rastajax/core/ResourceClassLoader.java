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
package com.kelveden.rastajax.core;

import com.kelveden.rastajax.core.raw.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Uses the <a href="http://jsr311.java.net/">JSR311 JAX-RS</a> annotations as a basis for loading classes as JAX-RS resources.
 */
class ResourceClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceClassLoader.class);
    private static final int UNDERLINE_LENGTH = 60;

    private static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATION_TYPES = new HashSet<Class<? extends Annotation>>() { {
        add(FormParam.class);
        add(PathParam.class);
        add(QueryParam.class);
        add(MatrixParam.class);
        add(HeaderParam.class);
    } };

    public ResourceClass loadResourceClassFrom(final Class<?> candidateResourceClass) {

        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));
        LOGGER.debug("Attempting to load class {} as a JAX-RS resource class...", candidateResourceClass.getName());
        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));

        final Set<Annotation> resourceAnnotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(candidateResourceClass);
        LOGGER.debug("Found class annotations {}.", resourceAnnotations.toString());

        String path = null;
        String[] produces = null;
        String[] consumes = null;

        for (Annotation annotation : resourceAnnotations) {
            if (annotation.annotationType() == Path.class) {
                path = ((Path) annotation).value();

                LOGGER.debug("Class URI template is '{}'.", path);

            } else if (annotation.annotationType() == Produces.class) {
                produces = ((Produces) annotation).value();

                LOGGER.debug("Class produces: {}.", StringUtils.join(produces, ","));

            } else if (annotation.annotationType() == Consumes.class) {
                consumes = ((Consumes) annotation).value();

                LOGGER.debug("Class consumes: {}.", StringUtils.join(consumes, ","));
            }
        }

        LOGGER.debug("Finding resource methods...");

        final List<ResourceClassMethod> methodsOnResource = loadMethodsFrom(candidateResourceClass);
        LOGGER.debug("Found {} resource methods.", methodsOnResource.size());

        if (methodsOnResource.size() == 0) {
            LOGGER.debug("Class is NOT a resource class.");

            return null;
        }

        LOGGER.debug("Class is a resource class.");

        return new ResourceClass(candidateResourceClass, path, methodsOnResource, arrayAsList(consumes), arrayAsList(produces));
    }

    private List<String> arrayAsList(final String[] array) {

        if (array != null) {
            return Arrays.asList(array);
        }

        return new ArrayList<String>();
    }

    private List<ResourceClassMethod> loadMethodsFrom(final Class<?> candidateResourceClass) {

        final List<ResourceClassMethod> methodsOnResource = new ArrayList<ResourceClassMethod>();

        final Method[] methods = candidateResourceClass.getDeclaredMethods();

        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                final ResourceClassMethod methodOnResource = loadMethodFrom(candidateResourceClass, method);

                if (methodOnResource != null) {
                    methodsOnResource.add(methodOnResource);
                }
            }
        }

        return methodsOnResource;
    }

    private ResourceClassMethod loadMethodFrom(final Class<?> candidateResourceClass, final Method method) {

        LOGGER.debug("Attempting to load method {} as a JAX-RS resource method...", method.getName());

        String requestMethodDesignator = null;
        String path = null;
        String[] produces = null;
        String[] consumes = null;
        Class<?> returnType = method.getReturnType();

        final Set<Annotation> methodAnnotations =  JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(candidateResourceClass, method);
        LOGGER.debug("Found method annotations {}.", methodAnnotations.toString());

        if (returnType != null) {
            LOGGER.debug("Method return type is {}.", returnType.getName());
        }

        for (Annotation annotation : methodAnnotations) {

            final HttpMethod httpMethodAnnotation = annotation.annotationType().getAnnotation(HttpMethod.class);

            if (httpMethodAnnotation != null) {
                requestMethodDesignator = httpMethodAnnotation.value();

                LOGGER.debug("Method request method designator is '{}'.", requestMethodDesignator);

            } else if (annotation.annotationType() == Path.class) {
                path = ((Path) annotation).value();

                LOGGER.debug("Method URI template '{}'.", path);

            } else if (annotation.annotationType() == Produces.class) {
                produces = ((Produces) annotation).value();

                LOGGER.debug("Method produces: {}.", StringUtils.join(produces, ","));

            } else if (annotation.annotationType() == Consumes.class) {
                consumes = ((Consumes) annotation).value();

                LOGGER.debug("Method consumes: {}.", StringUtils.join(consumes, ","));
            }
        }

        if ((path == null) && (requestMethodDesignator == null)) {
            LOGGER.debug("Method is NOT a resource method.");
            return null;
        }

        LOGGER.debug("Method is a resource method.");
        LOGGER.debug("Finding method parameters...");

        final List<ResourceClassMethodParameter> parameters = loadMethodParameters(candidateResourceClass, method);

        return createResourceClassMethod(candidateResourceClass, method, path, requestMethodDesignator, arrayAsList(consumes), arrayAsList(produces), parameters);
    }

    private List<ResourceClassMethodParameter> loadMethodParameters(Class<?> resourceClass, Method method) {

        final List<ResourceClassMethodParameter> parameters = new ArrayList<ResourceClassMethodParameter>();

        final Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            final ResourceClassMethodParameter parameter = loadMethodParameter(resourceClass, method, i);

            if (parameter != null) {
                parameters.add(parameter);
            }
        }
        return parameters;
    }

    private ResourceClassMethodParameter loadMethodParameter(final Class<?> clazz, final Method method, final int parameterIndex) {

        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(clazz, method, parameterIndex);
        LOGGER.debug("Found parameter annotations {}.", annotations.toString());

        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> parameterType = annotation.annotationType();
            final Class<?> type = method.getParameterTypes()[parameterIndex];

            if (PARAMETER_ANNOTATION_TYPES.contains(annotation.annotationType())) {
                try {
                    final String name = (String) annotation.annotationType().getMethod("value").invoke(annotation);

                    LOGGER.debug("Found {} parameter '{}' of type '{}'.", parameterType, name, type);

                    return new ResourceClassMethodParameter(name, parameterType, type);

                } catch (IllegalAccessException e) {
                    throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);

                } catch (InvocationTargetException e) {
                    throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);

                } catch (NoSuchMethodException e) {
                    throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);
                }
            }
        }

        return null;
    }

    private ResourceClassMethod createResourceClassMethod(final Class<?> resourceClass, final Method method, final String path, final String requestMethodDesignator, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters) {

        final boolean hasPath = (path != null);
        final boolean hasRequestMethodDesignator = (requestMethodDesignator != null);

        if (hasPath && hasRequestMethodDesignator) {
            return new SubResourceMethod(method.getName(), path, requestMethodDesignator, consumes, produces, parameters, method.getReturnType());

        } else if (hasPath && !hasRequestMethodDesignator) {

            final Class<?> returnType = method.getReturnType();

            if (returnType != null) {
                final ResourceClass subResource = loadResourceClassFrom(method.getReturnType());

                return new SubResourceLocator(method.getName(), path, consumes, produces, parameters, subResource);
            } else {
                throw new ResourceClassLoadingException("Method '" + method.getName() + "' appears to be a sub-resource locator but has no return type.");
            }

        } else if (!hasPath && hasRequestMethodDesignator) {
            return new ResourceMethod(method.getName(), requestMethodDesignator, consumes, produces, parameters, method.getReturnType(), resourceClass);

        } else {
            throw new ResourceClassLoadingException("Type for method '" + method.getName() + "' could not be determined.");
        }
    }
}
