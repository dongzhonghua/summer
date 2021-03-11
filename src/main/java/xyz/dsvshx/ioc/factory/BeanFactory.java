package xyz.dsvshx.ioc.factory;

import java.util.Map;

import xyz.dsvshx.ioc.entity.BeanDefinition;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
public interface BeanFactory {


    /**
     * 根据名称从容器中获取bean
     *
     * @param name bean的名字
     * @return bean实例对象
     */
    Object getBean(String name) throws Exception;

    /**
     * 根据类从容器中获取bean
     *
     * @param clazz bean的类对象
     * @return bean实例对象
     */
    Object getBean(Class<?> clazz) throws Exception;

    /**
     * 向工厂中注册bean定义
     *
     * @param name bean的名字
     * @param beanDefinition bean的定义对象
     * @throws Exception 可能出现的异常
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;

    /**
     * 获取所有注册的bean
     *
     * @return 所有bean的map
     */
    Map<String, Object> getAllBean() throws Exception;

}
