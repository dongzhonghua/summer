package xyz.dsvshx.ioc;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Application;
import xyz.dsvshx.ioc.annotation.mvc.WebApplication;
import xyz.dsvshx.ioc.context.ApplicationContext;
import xyz.dsvshx.ioc.controller.HelloController;
import xyz.dsvshx.ioc.service.HelloWorldService;
import xyz.dsvshx.ioc.service.ServiceA;
import xyz.dsvshx.ioc.service.ServiceB;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
@WebApplication
@Application(basepackage = "xyz.dsvshx.ioc")
public class Test {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SummerApplication.run(Test.class, args);

        // JsonApplicationContext applicationContext = new JsonApplicationContext("application.json");
        // applicationContext.init();
        // Robot aiRobot = (Robot) applicationContext.getBean("robot");
        // aiRobot.show();
        // ClassPathXmlApplicationContext applicationContext =
        //         new ClassPathXmlApplicationContext("application.xml");
        // ClassPathXmlApplicationContext applicationContext =
        //         new ClassPathXmlApplicationContext("base-package.xml");
        HelloWorldService helloWorldService = (HelloWorldService) applicationContext.getBean("helloWorldService");
        HelloWorldService helloWorldService1 = (HelloWorldService) applicationContext.getBean("helloWorldService");
        helloWorldService.saySomething();
        log.info(String.valueOf(helloWorldService1 == helloWorldService));
        HelloController c1 = (HelloController) applicationContext.getBean(HelloController.class);
        c1.say();
        HelloController c2 = (HelloController) applicationContext.getBean(HelloController.class);
        log.info(String.valueOf(c1 == c2));
        ServiceA beanA = (ServiceA) applicationContext.getBean(ServiceA.class);
        beanA.a();
        ServiceB beanB = (ServiceB) applicationContext.getBean(ServiceB.class);
        beanB.b();
        Map<String, Object> allBeans = applicationContext.getAllBeans();
        log.info(String.valueOf(allBeans));
    }
}
