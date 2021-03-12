package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Scope;
import xyz.dsvshx.ioc.annotation.Service;
import xyz.dsvshx.ioc.annotation.Value;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
@Service
@Scope("prototype")
public class HelloWorldServiceImpl implements HelloWorldService {
    @Value("Hello World")
    private String text;
    @Override
    public void saySomething() {
        log.info(text);
    }
}
