package xyz.dsvshx.ioc.reader;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.CaseFormat;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.Component;
import xyz.dsvshx.ioc.annotation.Qualifier;
import xyz.dsvshx.ioc.annotation.Scope;
import xyz.dsvshx.ioc.annotation.Service;
import xyz.dsvshx.ioc.annotation.Value;
import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.entity.BeanReference;
import xyz.dsvshx.ioc.entity.PropertyValue;
import xyz.dsvshx.ioc.io.ResourceLoader;
import xyz.dsvshx.ioc.util.ClassUtils;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
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
                switch (((Element) node).getTagName()) {
                    case "component-scan":
                        String basePackage = ((Element) node).getAttribute("base-package");
                        if (isNotBlank(basePackage)) {
                            parseAnnotation(basePackage);
                            return;
                        }
                        break;
                    case "bean":
                        processBeanDefinition((Element) node);
                        break;
                    case "aop-config":
                        processProxyDefinition((Element) node);
                        break;
                }
            }
        }
    }

    private void processProxyDefinition(Element node) {

    }

    public void parseAnnotation(String basePackage) {
        Set<Class<?>> classes = ClassUtils.findAllClassesByAnnotation(basePackage, Component.class, Service.class);
        for (Class<?> clazz : classes) {
            processAnnotationBeanDefinition(clazz);
        }
    }

    private void processAnnotationBeanDefinition(Class<?> clazz) {
        String name = "";
        if (clazz.getAnnotation(Component.class) != null) {
            name = clazz.getAnnotation(Component.class).name();
        }
        if (clazz.getAnnotation(Service.class) != null) {
            name = clazz.getAnnotation(Service.class).name();
        }
        if (StringUtils.isBlank(name)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                    interfaces.length == 1 ? interfaces[0].getSimpleName() : clazz.getSimpleName());
        }
        String className = clazz.getName();
        boolean singleton = true;
        if (clazz.isAnnotationPresent(Scope.class) && "prototype".equals(clazz.getAnnotation(Scope.class).value())) {
            singleton = false;
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        processAnnotationProperty(clazz, beanDefinition);
        processBeanDefinition(name, className, singleton, beanDefinition);
    }

    private void processBeanDefinition(String name, String className, boolean singleton,
            BeanDefinition beanDefinition) {
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

    private void processAnnotationProperty(Class<?> clazz, BeanDefinition beanDefinition) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            ArrayList<PropertyValue> propertyValues = new ArrayList<>();
            beanDefinition.setPropertyValues(propertyValues);
            if (field.isAnnotationPresent(Value.class)) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                if (isNotBlank(value)) {
                    // 优先进行值注入
                    propertyValues.add(PropertyValue.builder()
                            .value(value)
                            .name(name)
                            .build());
                }
            } else if (field.isAnnotationPresent(Autowired.class)) {
                if (field.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    String ref = qualifier.value();
                    if (isNotBlank(ref)) {
                        throw new IllegalArgumentException("the value of Qualifier should not be null!");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    propertyValues.add(PropertyValue.builder()
                            .value(beanReference)
                            .name(name)
                            .build());
                } else {
                    String ref = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, field.getType().getSimpleName());
                    BeanReference beanReference = new BeanReference(ref);
                    propertyValues.add(PropertyValue.builder()
                            .value(beanReference)
                            .name(name)
                            .build());
                }
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
        processBeanDefinition(name, className, singleton, beanDefinition);
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
