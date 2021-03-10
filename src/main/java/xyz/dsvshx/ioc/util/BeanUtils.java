package xyz.dsvshx.ioc.util;

import java.lang.reflect.Constructor;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Slf4j
public class BeanUtils {
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
