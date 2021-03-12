package xyz.dsvshx.ioc.service;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.Service;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
@Service
public class ServiceA {
    @Autowired
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
