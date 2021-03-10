package xyz.dsvshx.ioc.controller;

import xyz.dsvshx.ioc.service.HelloWorldService;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class HelloController {
    private HelloWorldService helloWorldService;

    public void say() {
        helloWorldService.saySomething();
    }
}
