package xyz.dsvshx.ioc.controller;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.mvc.GetMapping;
import xyz.dsvshx.ioc.annotation.mvc.PostMapping;
import xyz.dsvshx.ioc.annotation.mvc.RestController;
import xyz.dsvshx.ioc.service.HelloWorldService;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
@RestController("/index")
public class HelloController {
    @Autowired
    private HelloWorldService helloWorldService;

    @GetMapping("/say")
    public void say() {
        helloWorldService.saySomething();
    }

    @PostMapping("/put")
    public void post() {
        log.info("调用post方法");
    }
}
