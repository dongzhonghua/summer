package xyz.dsvshx.ioc.context;

import java.util.Map;

import xyz.dsvshx.ioc.factory.AbstractBeanFactory;
import xyz.dsvshx.ioc.reader.XmlBeanDefinitionReader;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    private final Object startupShutdownMonitor = new Object();
    private String location;

    public ClassPathXmlApplicationContext(String location) throws Exception {
        super();
        this.location = location;
        if (location != null) {
            refresh();
        }
    }

    protected void refresh() throws Exception {
        synchronized (startupShutdownMonitor) {
            // 获取所有的bean定义
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            // 初始化所有的bean
            prepareBeanFactory(beanFactory);
            this.beanFactory = beanFactory;
        }

    }

    /**
     * 将定义的beanDefinition初始化
     */
    private void prepareBeanFactory(AbstractBeanFactory beanFactory) throws Exception {
        beanFactory.populateBeans();
    }

    // protected AbstractBeanFactory obtainBeanFactory() throws Exception {
    //     XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
    //     xmlBeanDefinitionReader.loadBeanDefinitions(location);
    //     AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();
    //     for (Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry()
    //             .entrySet()) {
    //         beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
    //     }
    //     return beanFactory;
    // }

    @Override
    protected void processBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) throws Exception {
        xmlBeanDefinitionReader.loadBeanDefinitions(location);
    }

    @Override
    public Map<String, Object> getAllBeans() throws Exception {
        return beanFactory.getAllBean();
    }
}
