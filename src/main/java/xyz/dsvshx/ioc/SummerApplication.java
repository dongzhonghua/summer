package xyz.dsvshx.ioc;

import xyz.dsvshx.ioc.annotation.Application;
import xyz.dsvshx.ioc.annotation.mvc.WebApplication;
import xyz.dsvshx.ioc.context.ApplicationContext;
import xyz.dsvshx.ioc.context.ConfigurableApplicationContext;
import xyz.dsvshx.ioc.mvc.WebApplicationContext;

/**
 * @author dongzhonghua
 * Created on 2021-03-12
 */
public class SummerApplication {

    public static ApplicationContext run(Class<?> clazz, String[] args) throws Exception {
        Application annotation = clazz.getAnnotation(Application.class);
        String basepackage = clazz.getPackage().getName();
        if (annotation != null) {
            basepackage = annotation.basepackage();
        }
        if (clazz.isAnnotationPresent(WebApplication.class)) {
            // TODO: 2021/3/12 初始化controller
            return new WebApplicationContext(basepackage);
        }
        return new ConfigurableApplicationContext(basepackage);
    }
}
