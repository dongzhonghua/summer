package xyz.dsvshx.ioc.context;

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
public class ConfigurableApplicationContext extends ClassPathXmlApplicationContext {
    private String basepackage;

    public ConfigurableApplicationContext(String basepackage) throws Exception {
        // 这里复用了ClassPathXmlApplicationContext，但是感觉这里不是很优雅，传一个null进去有点蠢。
        super(null);
        this.basepackage = basepackage;
        refresh();
    }

    @Override
    protected AbstractBeanFactory obtainBeanFactory() throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.parseAnnotation(basepackage);
        AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();
        for (Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry()
                .entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        return beanFactory;
    }
}
