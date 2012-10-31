package com.kelveden.rastajax.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scrapes all JAX-RS {@link java.lang.annotation.Annotation}s from supplied entities.
 */
final class JaxRsAnnotationScraper {

    private JaxRsAnnotationScraper() {
    }

    /**
     * Scrapes the JAX-RS {@link java.lang.annotation.Annotation}s from the supplied {@link Class} and any super-types of it.
     *
     * @param clazz The {@link Class} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsAnnotationsFrom(final Class<?> clazz) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsAnnotationsFrom(clazz).values());
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

    /**
     * Scrapes the JAX-RS {@link java.lang.annotation.Annotation}s from the supplied {@link java.lang.reflect.Method}. Attempts to find a declaration for the specified {@link java.lang.reflect.Method} on the specified {@link Class};
     * then continues the attempts against the class's super-types.
     *
     * @param clazz The {@link Class} to try and find the method against.
     * @param method The {@link java.lang.reflect.Method} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsAnnotationsFrom(final Class<?> clazz, final Method method) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsAnnotationsFrom(clazz, method).values());
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

    /**
     * Scrapes the JAX-RS {@link java.lang.annotation.Annotation}s from the specified parameter for the supplied {@link java.lang.reflect.Method}.
     * Attempts to find a declaration for the specified {@link java.lang.reflect.Method} on the specified {@link Class}; then continues the attempts against the class's super-types.
     *
     * @param clazz The {@link Class} to try and find the method against.
     * @param method The {@link java.lang.reflect.Method} to scrape the {@link java.lang.annotation.Annotation}s from.
     * @param parameterIndex The index of the parameter on the method.
     * @return The {@link java.lang.annotation.Annotation}s as a {@link java.util.Set}.
     */
    public static Set<Annotation> scrapeJaxRsAnnotationsFrom(final Class<?> clazz, final Method method, final int parameterIndex) {
        return new HashSet<Annotation>(scrapeUniqueJaxRsAnnotationsFrom(clazz, method, parameterIndex).values());
    }

    private static Map<String, Annotation> scrapeUniqueJaxRsAnnotationsFrom(final Class<?> clazz, final Method method, final int parameterIndex) {

        final Map<String, Annotation> annotationTypeToInstanceMap = new HashMap<String, Annotation>();

        final Method clazzMethod = getMethodOnClass(clazz, method);

        if (clazzMethod != null) {
            for (Annotation annotation : clazzMethod.getParameterAnnotations()[parameterIndex]) {
                if (isJaxRsAnnotation(annotation)) {
                    annotationTypeToInstanceMap.put(annotation.annotationType().getName(), annotation);
                }
            }
        }

        if (annotationTypeToInstanceMap.size() == 0) {
            for (Class<?> superType : clazz.getInterfaces()) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(superType, method, parameterIndex));
            }

            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                annotationTypeToInstanceMap.putAll(scrapeUniqueJaxRsAnnotationsFrom(superClass, method, parameterIndex));
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
}
