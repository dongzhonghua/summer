package xyz.dsvshx.ioc.mvc;

import static xyz.dsvshx.ioc.mvc.RequestHandler.initHandlerMapping;
import static xyz.dsvshx.ioc.mvc.RequestHandler.scanner;
import static xyz.dsvshx.ioc.reader.XmlBeanDefinitionReader.processAnnotationProperty;

import java.util.Set;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Scope;
import xyz.dsvshx.ioc.context.ClassPathXmlApplicationContext;
import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.factory.AbstractBeanFactory;
import xyz.dsvshx.ioc.reader.XmlBeanDefinitionReader;
import xyz.dsvshx.ioc.server.NettyHttpServer;
import xyz.dsvshx.ioc.util.ClassUtils;

/**
 * @author dongzhonghua
 * Created on 2021-03-12
 */
@Slf4j
public class WebApplicationContext extends ClassPathXmlApplicationContext implements DispatcherServlet {
    private String basepackage;
    private Set<Class<?>> webClassSet;


    public WebApplicationContext(String basepackage) throws Exception {
        super(null);
        this.basepackage = basepackage;
        startWebServer();
    }

    protected void startWebServer() throws Exception {
        // 启动spring容器
        // 把除了web相关的bean注册了
        this.beanFactory = obtainBeanFactory();
        webClassSet = scanner(basepackage);
        // 注册web相关的bean。
        registerWebBeanDefinition();
        // 初始化所有的bean
        ((AbstractBeanFactory) beanFactory).populateBeans();

        // 初始化所有mapping
        initHandlerMapping(getAllBeans());
        // 最后初始化netty服务器
        log.info("所有的bean：{}", getAllBeans());
        new NettyHttpServer().start(this);
    }

    private void registerWebBeanDefinition() throws Exception {
        for (Class<?> clazz : webClassSet) {
            BeanDefinition beanDefinition = new BeanDefinition();
            String name = ClassUtils.getLowerCamelName(clazz);
            beanDefinition.setId(name);
            String className = clazz.getName();
            beanDefinition.setBeanClassName(className);
            boolean singleton = true;
            if (clazz.isAnnotationPresent(Scope.class) && "prototype"
                    .equals(clazz.getAnnotation(Scope.class).value())) {
                singleton = false;
            }
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
            processAnnotationProperty(clazz, beanDefinition);
            beanFactory.registerBeanDefinition(name, beanDefinition);
        }
    }

    @Override
    protected void processBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) throws Exception {
        xmlBeanDefinitionReader.parseAnnotation(basepackage);
    }

    @Override
    public FullHttpResponse handle(FullHttpRequest fullHttpRequest) {
        return RequestHandler.doHandler(this, fullHttpRequest);
    }
}
