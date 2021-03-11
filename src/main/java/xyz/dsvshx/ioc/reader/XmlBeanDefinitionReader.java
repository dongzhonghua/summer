package xyz.dsvshx.ioc.reader;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.entity.BeanReference;
import xyz.dsvshx.ioc.entity.PropertyValue;
import xyz.dsvshx.ioc.io.ResourceLoader;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * 从配置文件中读取bean定义
     *
     * @param location 配置文件路径
     */
    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        // 解析xml document并注册bean
        registerBeanDefinitions(document);
        inputStream.close();
    }

    public void registerBeanDefinitions(Document document) {
        Element root = document.getDocumentElement();
        // 从文件根递归解析
        parseBeanDefinitions(root);
    }

    protected void parseBeanDefinitions(Element root) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                processBeanDefinition((Element) node);
            }
        }
    }

    protected void processBeanDefinition(Element ele) {
        String name = ele.getAttribute("id");
        String className = ele.getAttribute("class");
        boolean singleton = true;
        // 默认bean为单例，只有配置的才会变成多例
        if (ele.hasAttribute("scope") && "prototype".equals(ele.getAttribute("scope"))) {
            singleton = false;
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(ele, beanDefinition);
        beanDefinition.setId(name);
        beanDefinition.setBeanClassName(className);
        beanDefinition.setIsSingleton(singleton);
        try {
            Class<?> beanClass = Class.forName(className);
            beanDefinition.setBeanClass(beanClass);
            Class<?>[] interfaces = beanClass.getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                beanDefinition.setInterfaces(interfaces);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        getRegistry().put(name, beanDefinition);
    }

    private void processProperty(Element ele, BeanDefinition beanDefinition) {
        NodeList propertyNode = ele.getElementsByTagName("property");
        ArrayList<PropertyValue> propertyValues = new ArrayList<>();
        beanDefinition.setPropertyValues(propertyValues);
        for (int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    // 优先进行值注入
                    propertyValues.add(PropertyValue.builder()
                            .value(value)
                            .name(name)
                            .build());
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException(
                                "Configuration problem: <property> element for property '" + name
                                        + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    propertyValues.add(PropertyValue.builder()
                            .value(beanReference)
                            .name(name)
                            .build());
                }
            }
        }
    }


}
