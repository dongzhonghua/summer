package xyz.dsvshx.ioc.context;

import java.util.Map.Entry;

import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.factory.AbstractBeanFactory;
import xyz.dsvshx.ioc.factory.AutowiredCapableBeanFactory;
import xyz.dsvshx.ioc.factory.BeanFactory;
import xyz.dsvshx.ioc.io.ResourceLoader;
import xyz.dsvshx.ioc.reader.XmlBeanDefinitionReader;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    protected BeanFactory beanFactory;

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    protected AbstractBeanFactory obtainBeanFactory() throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        processBeanDefinitionReader(xmlBeanDefinitionReader);
        AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();
        for (Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry()
                .entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        return beanFactory;
    }

    protected abstract void processBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader)
            throws Exception;
}
