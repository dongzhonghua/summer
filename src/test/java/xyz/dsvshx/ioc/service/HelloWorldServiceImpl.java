package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
public class HelloWorldServiceImpl implements HelloWorldService {
    private String text;
    @Override
    public void saySomething() {
        log.info(text);
    }
}
