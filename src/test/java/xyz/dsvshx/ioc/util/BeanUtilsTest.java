package xyz.dsvshx.ioc.util;

import org.junit.jupiter.api.Test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import xyz.dsvshx.ioc.service.ServiceB;

/**
 * @author dongzhonghua
 * Created on 2021-03-11
 */
class BeanUtilsTest {
    @Test
    public void proxyTest() {
        // TODO: 2021/3/11  spring 的aop使用cglib代理时。如果一个类的属性也是代理类则spring会加一个callback。
        //  这里的callback的原理还不是很明白。应该是在CglibAopProxy，具体的到时候再看，不过给属性赋值好像也能实现这个功能。
        Enhancer enhancerB = new Enhancer();
        enhancerB.setSuperclass(ServiceB.class);
        enhancerB.setCallback(NoOp.INSTANCE);
    }

}