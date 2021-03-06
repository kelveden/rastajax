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
import java.lang.reflect.Field;
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

    public static final Set<Class<? extends Annotation>> PARAMETER_TYPE_ANNOTATIONS = new HashSet<Class<? extends Annotation>>() { {
        add(FormParam.class);
        add(PathParam.class);
        add(QueryParam.class);
        add(MatrixParam.class);
        add(HeaderParam.class);
        add(CookieParam.class);
    } };

    public ResourceClass loadResourceClassFrom(final Class<?> candidateResourceClass) {

        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));
        LOGGER.debug("Attempting to load class {} as a JAX-RS resource class...", candidateResourceClass.getName());
        LOGGER.debug(StringUtils.repeat("-", UNDERLINE_LENGTH));

        final Set<Annotation> resourceAnnotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(candidateResourceClass);
        LOGGER.debug("Found class annotations {}.", resourceAnnotations.toString());

        String uriTemplate = null;
        String[] produces = null;
        String[] consumes = null;

        for (Annotation annotation : resourceAnnotations) {
            if (annotation.annotationType() == Path.class) {
                uriTemplate = ((Path) annotation).value();

                LOGGER.debug("Class URI template is '{}'.", uriTemplate);

            } else if (annotation.annotationType() == Produces.class) {
                produces = ((Produces) annotation).value();

                LOGGER.debug("Class produces: {}.", StringUtils.join(produces, ","));

            } else if (annotation.annotationType() == Consumes.class) {
                consumes = ((Consumes) annotation).value();

                LOGGER.debug("Class consumes: {}.", StringUtils.join(consumes, ","));
            }
        }

        LOGGER.debug("Finding resource methods...");

        final List<ResourceClassMethod> methodsOnResource = loadMethods(candidateResourceClass);
        LOGGER.debug("Found {} resource methods.", methodsOnResource.size());

        if (methodsOnResource.size() == 0) {
            LOGGER.debug("Class is NOT a resource class.");

            return null;
        }

        LOGGER.debug("Class is a resource class.");

        LOGGER.debug("Finding fields...");
        final List<Parameter> fields = loadClassFields(candidateResourceClass);
        LOGGER.debug("Found {} fields.", fields.size());

        LOGGER.debug("Finding properties...");
        final List<Parameter> properties = loadClassProperties(candidateResourceClass);
        LOGGER.debug("Found {} properties.", properties.size());

        fields.addAll(properties);

        return new ResourceClass(candidateResourceClass, uriTemplate, methodsOnResource, arrayAsList(consumes), arrayAsList(produces), fields);
    }

    private List<Parameter> loadClassFields(final Class<?> resourceClass) {

        final String logPrefix = " |-";

        final List<Parameter> fields = new ArrayList<Parameter>();

        for (Field field : resourceClass.getFields()) {
            final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(field);

            try {
                final Parameter parameter = buildParameterFromJaxRsAnnotations(annotations, field.getType());

                if (parameter != null) {
                    LOGGER.debug("{} Found {} field '{}' of type '{}'.", logPrefix, parameter.getJaxRsAnnotationType().getSimpleName(), parameter.getName(), parameter.getType().getName());

                    fields.add(parameter);
                }

            } catch (IllegalAccessException e) {
                throw new ResourceClassLoadingException(String.format("Could not load field '%s' on class '%s'", field.getName(), resourceClass.getName()), e);

            } catch (InvocationTargetException e) {
                throw new ResourceClassLoadingException(String.format("Could not load field '%s' on class '%s'", field.getName(), resourceClass.getName()), e);

            } catch (NoSuchMethodException e) {
                throw new ResourceClassLoadingException(String.format("Could not load field '%s' on class '%s'", field.getName(), resourceClass.getName()), e);
            }
        }

        return fields;
    }

    private List<Parameter> loadClassProperties(final Class<?> resourceClass) {

        final String logPrefix = " |-";

        final List<Parameter> fields = new ArrayList<Parameter>();

        for (Method method : resourceClass.getDeclaredMethods()) {
            final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(resourceClass, method);

            try {
                final Parameter parameter = buildParameterFromJaxRsAnnotations(annotations, method.getReturnType());

                if (parameter != null) {
                    LOGGER.debug("{} Found {} property '{}' of type '{}'.", logPrefix, parameter.getJaxRsAnnotationType().getSimpleName(), parameter.getName(), parameter.getType().getName());

                    fields.add(parameter);
                } else {
                    LOGGER.debug("{} Method {} was not annotated with any annotations that describe the JAX-RS parameter type and so will be ignored.", logPrefix, method.getName());
                }

            } catch (IllegalAccessException e) {
                throw new ResourceClassLoadingException(String.format("Could not load property '%s' on class '%s'", method.getName(), resourceClass.getName()), e);

            } catch (InvocationTargetException e) {
                throw new ResourceClassLoadingException(String.format("Could not load property '%s' on class '%s'", method.getName(), resourceClass.getName()), e);

            } catch (NoSuchMethodException e) {
                throw new ResourceClassLoadingException(String.format("Could not load property'%s' on class '%s'", method.getName(), resourceClass.getName()), e);
            }
        }

        return fields;
    }

    private List<String> arrayAsList(final String[] array) {

        if (array != null) {
            return Arrays.asList(array);
        }

        return new ArrayList<String>();
    }

    private List<ResourceClassMethod> loadMethods(final Class<?> candidateResourceClass) {

        final List<ResourceClassMethod> methodsOnResource = new ArrayList<ResourceClassMethod>();

        Method[] methods;
        try {
            methods = candidateResourceClass.getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            LOGGER.warn("Could not process candidate resource class {} as a class referenced in it could not be found.", candidateResourceClass.getName(), e);
            return methodsOnResource;
        }

        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                final ResourceClassMethod methodOnResource = loadMethod(candidateResourceClass, method);

                if (methodOnResource != null) {
                    methodsOnResource.add(methodOnResource);
                }
            }
        }

        return methodsOnResource;
    }

    private ResourceClassMethod loadMethod(final Class<?> candidateResourceClass, final Method method) {

        final String logPrefix = " |-";

        LOGGER.debug("{} Attempting to load method {} as a JAX-RS resource method...", logPrefix, method.getName());

        String requestMethodDesignator = null;
        String uriTemplate = null;
        String[] produces = null;
        String[] consumes = null;
        Class<?> returnType = method.getReturnType();

        final Set<Annotation> methodAnnotations =  JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(candidateResourceClass, method);
        LOGGER.debug("{} Found method annotations {}.", logPrefix, methodAnnotations.toString());

        if (returnType != null) {
            LOGGER.debug("{} Method return type is {}.", logPrefix, returnType.getName());
        }

        for (Annotation annotation : methodAnnotations) {

            final HttpMethod httpMethodAnnotation = annotation.annotationType().getAnnotation(HttpMethod.class);

            if (httpMethodAnnotation != null) {
                requestMethodDesignator = httpMethodAnnotation.value();

                LOGGER.debug("{} Method request method designator is '{}'.", logPrefix, requestMethodDesignator);

            } else if (annotation.annotationType() == Path.class) {
                uriTemplate = ((Path) annotation).value();

                LOGGER.debug("{} Method URI template '{}'.", logPrefix, uriTemplate);

            } else if (annotation.annotationType() == Produces.class) {
                produces = ((Produces) annotation).value();

                LOGGER.debug("{} Method produces: {}.", logPrefix, StringUtils.join(produces, ","));

            } else if (annotation.annotationType() == Consumes.class) {
                consumes = ((Consumes) annotation).value();

                LOGGER.debug("{} Method consumes: {}.", logPrefix, StringUtils.join(consumes, ","));
            }
        }

        if ((uriTemplate == null) && (requestMethodDesignator == null)) {
            LOGGER.debug("{} Method is NOT a resource method.", logPrefix);
            return null;
        }

        LOGGER.debug("{} Method is a resource method.", logPrefix);
        LOGGER.debug("{} Finding method parameters...", logPrefix);

        final List<Parameter> parameters = loadMethodParameters(candidateResourceClass, method);

        return createResourceClassMethod(method, uriTemplate, requestMethodDesignator, arrayAsList(consumes), arrayAsList(produces), parameters);
    }

    private List<Parameter> loadMethodParameters(Class<?> resourceClass, Method method) {

        final String logPrefix = " |-";

        final List<Parameter> parameters = new ArrayList<Parameter>();

        final Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            final Parameter parameter = loadMethodParameter(resourceClass, method, i);

            if (parameter != null) {
                LOGGER.debug("{} Found {} parameter '{}' of type '{}'.", logPrefix, parameter.getJaxRsAnnotationType().getSimpleName(), parameter.getName(), parameter.getType().getName());

                parameters.add(parameter);
            }
        }
        return parameters;
    }

    private Parameter loadMethodParameter(final Class<?> clazz, final Method method, final int parameterIndex) {

        final String logPrefix = " |---";

        final Set<Annotation> annotations = JaxRsAnnotationScraper.scrapeJaxRsAnnotationsFrom(clazz, method, parameterIndex);
        LOGGER.debug("{} Found parameter annotations {}.", logPrefix, annotations.toString());

        final Class<?> type = method.getParameterTypes()[parameterIndex];

        try {
            return buildParameterFromJaxRsAnnotations(annotations, type);

        } catch (IllegalAccessException e) {
            throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);

        } catch (InvocationTargetException e) {
            throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);

        } catch (NoSuchMethodException e) {
            throw new ResourceClassLoadingException(String.format("Could not load resource method parameter at index %s on method '%s' on class '%s'", parameterIndex, method.getName(), clazz.getName()), e);
        }
    }

    private Parameter buildParameterFromJaxRsAnnotations(final Set<Annotation> annotations, final Class<?> parameterType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<? extends Annotation> parameterAnnotationType = null;
        String parameterName = null;

        for (Annotation annotation : annotations) {
            if (PARAMETER_TYPE_ANNOTATIONS.contains(annotation.annotationType())) {
                parameterAnnotationType = annotation.annotationType();
                parameterName = (String) annotation.annotationType().getMethod("value").invoke(annotation);
            }
        }

        if ((parameterName != null) && (parameterAnnotationType != null)) {
            return new Parameter(parameterName, parameterAnnotationType, parameterType);

        } else {
            return null;
        }
    }

    private ResourceClassMethod createResourceClassMethod(final Method method, final String uriTemplate, final String requestMethodDesignator, final List<String> consumes, final List<String> produces, final List<Parameter> parameters) {

        final boolean hasPath = (uriTemplate != null);
        final boolean hasRequestMethodDesignator = (requestMethodDesignator != null);

        if (hasPath && hasRequestMethodDesignator) {
            return new SubResourceMethod(method.getName(), uriTemplate, requestMethodDesignator, consumes, produces, parameters, method.getReturnType());

        } else if (hasPath && !hasRequestMethodDesignator) {

            final Class<?> returnType = method.getReturnType();

            if (returnType != null) {
                final ResourceClass subResource = loadResourceClassFrom(method.getReturnType());

                return new SubResourceLocator(method.getName(), uriTemplate, consumes, produces, parameters, subResource);
            } else {
                throw new ResourceClassLoadingException("Method '" + method.getName() + "' appears to be a sub-resource locator but has no return type.");
            }

        } else if (!hasPath && hasRequestMethodDesignator) {
            return new ResourceMethod(method.getName(), requestMethodDesignator, consumes, produces, parameters, method.getReturnType());

        } else {
            throw new ResourceClassLoadingException("Type for method '" + method.getName() + "' could not be determined.");
        }
    }
}
