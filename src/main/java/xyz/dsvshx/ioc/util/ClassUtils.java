package xyz.dsvshx.ioc.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
public class ClassUtils {

    public static ClassLoader getDefultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className) {
        try {
            return getDefultClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<Class<?>> findAllClassesByPackage(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class);
    }

    public static Set<Class<?>> findAllClassesByAnnotation(String packageName,
            Class<? extends Annotation>... annotations) {
        Reflections reflections =
                new Reflections(packageName, new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<?>> res = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            res.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return res;
    }
}
