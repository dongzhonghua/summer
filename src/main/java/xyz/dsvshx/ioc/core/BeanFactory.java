package xyz.dsvshx.ioc.core;

import java.util.Map;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
public interface BeanFactory {
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;

    Map<String, Object> getAllBean() throws Exception;
}
