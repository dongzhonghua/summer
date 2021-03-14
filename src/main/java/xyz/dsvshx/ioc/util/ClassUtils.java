package xyz.dsvshx.ioc.util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.google.common.base.CaseFormat;

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

    @SafeVarargs
    public static Set<Class<?>> findAllClassesByAnnotation(String packageName,
            Class<? extends Annotation>... annotations) {
        Reflections reflections =
                new Reflections(packageName, new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<?>> res = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            res.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return res.stream().filter(clazz -> !clazz.isAnnotation()).collect(Collectors.toSet());
    }

    public static String getLowerCamelName(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }

    public static String getLowerCamelName(Class<?> clazz) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
    }

    public static Object convert(Class<?> targetType, String s) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(s);
        return editor.getValue();
    }
}
