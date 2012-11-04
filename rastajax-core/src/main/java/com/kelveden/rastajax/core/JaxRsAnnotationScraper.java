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

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scrapes all <a href="http://jsr311.java.net/">JAX-RS</a> {@link java.lang.annotation.Annotation}s from supplied items.
 */
final class JaxRsAnnotationScraper {

    public static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATION_TYPES = new HashSet<Class<? extends Annotation>>() { {
        add(FormParam.class);
        add(PathParam.class);
        add(QueryParam.class);
        add(MatrixParam.class);
        add(HeaderParam.class);
        add(CookieParam.class);
    } };

    private JaxRsAnnotationScraper() {
    }

    /**
     * Scrapes the <a href="http://jsr311.java.net/">JAX-RS</a>  {@link java.lang.annotation.Annotation}s from the
     * supplied {@link Class} and any super-types of it.
     *
     * @param clazz The {@link Class} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsAnnotationsFrom(final Class<?> clazz) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsAnnotationsFrom(clazz).values());
    }

    /**
     * Scrapes the <a href="http://jsr311.java.net/">JAX-RS</a>  {@link java.lang.annotation.Annotation}s from the
     * supplied {@link java.lang.reflect.Method}. Attempts to find a declaration for the specified {@link java.lang.reflect.Method}
     * on the specified {@link Class}; then continues the attempts against the class's super-types.
     *
     * @param clazz The {@link Class} to try and find the method against.
     * @param method The {@link java.lang.reflect.Method} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsAnnotationsFrom(final Class<?> clazz, final Method method) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsAnnotationsFrom(clazz, method).values());
    }

    /**
     * Scrapes the <a href="http://jsr311.java.net/">JAX-RS</a>  {@link java.lang.annotation.Annotation}s from
     * the specified parameter for the supplied {@link java.lang.reflect.Method}. Attempts to find a declaration
     * for the specified {@link java.lang.reflect.Method} on the specified {@link Class}; then continues the attempts
     * against the class's super-types.
     *
     * @param clazz The {@link Class} to try and find the method against.
     * @param method The {@link java.lang.reflect.Method} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @param parameterIndex The index of the parameter on the method.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsParameterAnnotationsFrom(final Class<?> clazz, final Method method, final int parameterIndex) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsParameterAnnotationsFrom(clazz, method, parameterIndex).values());
    }

    /**
     * Scrapes the <a href="http://jsr311.java.net/">JAX-RS</a> {@link java.lang.annotation.Annotation}s relevant against a field or property
     * from the supplied {@link Field}.
     *
     * @param field The {@link Field} to scan for annotations.
     *
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsParameterAnnotationsFrom(final Field field) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsParameterAnnotationsFrom(field).values());
    }

    /**
     * Scrapes the <a href="http://jsr311.java.net/">JAX-RS</a> {@link java.lang.annotation.Annotation}s relevant against a field or property
     * from the supplied {@link Method} that may be a property getter.
     *
     * @param clazz The {@link Class} that the {@link Method} belongs to.
     * @param method The {@link Method} to scan for annotations.
     *
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsParameterAnnotationsFrom(Class<?> clazz, Method method) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsParameterAnnotationsFrom(clazz, method).values());
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsAnnotationsFrom(final Class<?> clazz) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (isJaxRsAnnotation(annotation)) {
                annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
            }
        }

        if (annotationTypeToInstanceMap.size() == 0) {
            for (Class<?> implementedInterface : clazz.getInterfaces()) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(implementedInterface));
            }

            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(superClass));
            }
        }

        return annotationTypeToInstanceMap;
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsAnnotationsFrom(final Class<?> clazz, final Method method) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        final Method clazzMethod = getMethodOnClass(clazz, method);

        if (clazzMethod != null) {
            for (Annotation annotation : clazzMethod.getDeclaredAnnotations()) {
                if (isJaxRsAnnotation(annotation)) {
                    annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
                }
            }
        }

        if (annotationTypeToInstanceMap.size() == 0) {
            for (Class<?> superType : clazz.getInterfaces()) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(superType, method));
            }

            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(superClass, method));
            }
        }

        return annotationTypeToInstanceMap;
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsParameterAnnotationsFrom(final Class<?> clazz, final Method method) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        final Method clazzMethod = getMethodOnClass(clazz, method);

        if (clazzMethod != null) {
            for (Annotation annotation : clazzMethod.getDeclaredAnnotations()) {
                if (isJaxRsParameterAnnotation(annotation)) {
                    annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
                }
            }
        }

        if (annotationTypeToInstanceMap.size() == 0) {
            for (Class<?> superType : clazz.getInterfaces()) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsParameterAnnotationsFrom(superType, method));
            }

            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsParameterAnnotationsFrom(superClass, method));
            }
        }

        return annotationTypeToInstanceMap;
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsParameterAnnotationsFrom(final Class<?> clazz, final Method method, final int parameterIndex) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        final Method clazzMethod = getMethodOnClass(clazz, method);

        if (clazzMethod != null) {
            for (Annotation annotation : clazzMethod.getParameterAnnotations()[parameterIndex]) {
                if (isJaxRsParameterAnnotation(annotation)) {
                    annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
                }
            }
        }

        if (annotationTypeToInstanceMap.size() == 0) {
            for (Class<?> superType : clazz.getInterfaces()) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsParameterAnnotationsFrom(superType, method, parameterIndex));
            }

            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsParameterAnnotationsFrom(superClass, method, parameterIndex));
            }
        }

        return annotationTypeToInstanceMap;
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsParameterAnnotationsFrom(final Field field) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if (isJaxRsParameterAnnotation(annotation)) {
                annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
            }
        }

        return annotationTypeToInstanceMap;
    }

    private static Method getMethodOnClass(final Class<?> clazz, final Method method) {
        try {
            return clazz.getMethod(method.getName(), method.getParameterTypes());
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    private static boolean isJaxRsAnnotation(final Annotation annotation) {
        return annotation.annotationType().getPackage().getName().startsWith("javax.ws.rs");
    }

    private static boolean isJaxRsParameterAnnotation(final Annotation annotation) {
        return PARAMETER_ANNOTATION_TYPES.contains(annotation.annotationType());
    }
}
