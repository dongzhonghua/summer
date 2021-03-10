package xyz.dsvshx.ioc.util;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
public class ClassUtils {

    public static ClassLoader getDefultClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }
    public static Class loadClass(String className){
        try {
            return getDefultClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Class<?>> findAllClassesByPackage(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class);
    }

    public Set<Class<?>> findAllClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
