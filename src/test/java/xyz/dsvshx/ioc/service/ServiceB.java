package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
public class ServiceB {
    private ServiceA serviceA;

    public void b() {
        serviceA.a();
    }
}
