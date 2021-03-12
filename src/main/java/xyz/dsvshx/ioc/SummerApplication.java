package xyz.dsvshx.ioc;

import xyz.dsvshx.ioc.annotation.Application;
import xyz.dsvshx.ioc.context.ConfigurableApplicationContext;

/**
 * @author dongzhonghua
 * Created on 2021-03-12
 */
public class SummerApplication {

    public static ConfigurableApplicationContext run(Class<?> clazz, String[] args) throws Exception {
        Application annotation = clazz.getAnnotation(Application.class);
        String basepackage = clazz.getPackage().getName();
        if (annotation != null) {
            basepackage = annotation.basepackage();
        }
        return new ConfigurableApplicationContext(basepackage);
    }
}
