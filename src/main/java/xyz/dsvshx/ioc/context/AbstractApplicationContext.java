package xyz.dsvshx.ioc.context;

import xyz.dsvshx.ioc.factory.BeanFactory;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    BeanFactory beanFactory;

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }
    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }
}
