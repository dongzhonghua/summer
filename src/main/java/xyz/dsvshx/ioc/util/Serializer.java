package xyz.dsvshx.ioc.util;

/**
 * @author dongzhonghua
 * Created on 2021-03-13
 */
public interface Serializer {
    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
