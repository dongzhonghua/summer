package xyz.dsvshx.ioc.context;

import java.util.Map;
import java.util.Map.Entry;

import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.factory.AbstractBeanFactory;
import xyz.dsvshx.ioc.factory.AutowiredCapableBeanFactory;
import xyz.dsvshx.ioc.io.ResourceLoader;
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

    public void refresh() throws Exception {
        synchronized (startupShutdownMonitor) {
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            prepareBeanFactory(beanFactory);
            this.beanFactory = beanFactory;
        }

    }

    private void prepareBeanFactory(AbstractBeanFactory beanFactory) throws Exception {
        beanFactory.populateBeans();
    }

    protected AbstractBeanFactory obtainBeanFactory() throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions(location);
        AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();
        for (Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry()
                .entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        return beanFactory;
    }

    @Override
    public Map<String, Object> getAllBeans() throws Exception {
        return beanFactory.getAllBean();
    }
}
