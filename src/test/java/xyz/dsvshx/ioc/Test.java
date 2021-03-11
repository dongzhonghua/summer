package xyz.dsvshx.ioc;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.context.ClassPathXmlApplicationContext;
import xyz.dsvshx.ioc.controller.HelloController;
import xyz.dsvshx.ioc.service.HelloWorldService;
import xyz.dsvshx.ioc.service.ServiceA;
import xyz.dsvshx.ioc.service.ServiceB;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        // JsonApplicationContext applicationContext = new JsonApplicationContext("application.json");
        // applicationContext.init();
        // Robot aiRobot = (Robot) applicationContext.getBean("robot");
        // aiRobot.show();
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("application.xml");
        HelloWorldService helloWorldService = (HelloWorldService) applicationContext.getBean("helloWorldService");
        HelloWorldService helloWorldService1 = (HelloWorldService) applicationContext.getBean("helloWorldService");
        helloWorldService.saySomething();
        log.info(String.valueOf(helloWorldService1 == helloWorldService));

        HelloController c1 = (HelloController) applicationContext.getBean(HelloController.class);
        c1.say();
        HelloController c2 = (HelloController) applicationContext.getBean(HelloController.class);
        log.info(String.valueOf(c1 == c2));
        Map<String, Object> allBeans = applicationContext.getAllBeans();

        ServiceA beanA = (ServiceA) applicationContext.getBean(ServiceA.class);
        beanA.a();
        ServiceB beanB = (ServiceB) applicationContext.getBean(ServiceB.class);

        beanB.b();
    }
}
