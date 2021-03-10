package xyz.dsvshx.ioc.service;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class HelloWorldServiceImpl implements HelloWorldService {
    private String text;
    @Override
    public void saySomething() {
        System.out.println(text);
    }
}
