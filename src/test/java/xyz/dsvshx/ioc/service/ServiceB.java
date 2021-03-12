package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.Component;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
@Component
public class ServiceB {
    @Autowired
    private ServiceA serviceA;

    public void b() {
        serviceA.a();
    }
}
