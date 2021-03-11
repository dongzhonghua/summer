package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
public class ServiceA {
    private ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    public ServiceA() {

    }

    public void a() {
        log.info(">>>>>>>>>>>>> 调用service a");
    }
}
