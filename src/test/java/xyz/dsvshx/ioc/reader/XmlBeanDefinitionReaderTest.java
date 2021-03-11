package xyz.dsvshx.ioc.reader;

import org.junit.jupiter.api.Test;

import xyz.dsvshx.ioc.io.ResourceLoader;

/**
 * @author dongzhonghua
 * Created on 2021-03-11
 */
class XmlBeanDefinitionReaderTest {

    @Test
    void loadBeanDefinitions() throws Exception {
        ResourceLoader resourceLoader = new ResourceLoader();

        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(resourceLoader);
        xmlBeanDefinitionReader.loadBeanDefinitions("application.xml");
        System.out.println(xmlBeanDefinitionReader.getRegistry());

    }
}