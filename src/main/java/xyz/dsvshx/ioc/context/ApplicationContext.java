package xyz.dsvshx.ioc.context;

import java.util.Map;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public interface ApplicationContext {
    Object getBean(Class<?> clazz) throws Exception;

    Object getBean(String beanName) throws Exception;

    /**
     * 获取所有注册的bean
     *
     * @return 所有bean的map
     */
    Map<String, Object> getAllBeans() throws Exception;

}
