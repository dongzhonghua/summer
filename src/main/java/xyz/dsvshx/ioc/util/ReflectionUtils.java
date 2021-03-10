package xyz.dsvshx.ioc.util;

import java.lang.reflect.Field;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
public class ReflectionUtils {

    public static void injectField(Field field, Object obj, Object value) throws IllegalAccessException {
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }
}
