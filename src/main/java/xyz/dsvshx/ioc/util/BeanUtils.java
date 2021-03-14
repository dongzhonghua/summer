package xyz.dsvshx.ioc.util;

import static xyz.dsvshx.ioc.util.ClassUtils.getLowerCamelName;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import xyz.dsvshx.ioc.annotation.Component;
import xyz.dsvshx.ioc.annotation.Service;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Slf4j
public class BeanUtils {

    public static String getBeanName(Class<?> clazz) {
        String name = "";
        if (clazz.getAnnotation(Component.class) != null) {
            name = clazz.getAnnotation(Component.class).name();
        }
        if (clazz.getAnnotation(Service.class) != null) {
            name = clazz.getAnnotation(Service.class).name();
        }
        if (StringUtils.isBlank(name)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            String interfaceName = interfaces.length == 1 ? interfaces[0].getSimpleName() : clazz.getSimpleName();
            name = getLowerCamelName(interfaceName);
        }
        return name;
    }

    public static <T> T getInstanceByCglib(Class<T> clz, Constructor constructor, Object[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(NoOp.INSTANCE);
        if (constructor == null) {
            return (T) enhancer.create();
        } else {
            return (T) enhancer.create(constructor.getParameterTypes(), args);
        }
    }

    public static <T> T getInstance(Class<T> clz, Constructor<T> constructor, Object[] args) {
        try {
            if (constructor == null) {

                return clz.newInstance();

            } else {
                return constructor.newInstance(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建对象失败");
            return null;
        }
    }
}
