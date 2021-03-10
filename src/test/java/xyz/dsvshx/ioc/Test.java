package xyz.dsvshx.ioc;

import xyz.dsvshx.ioc.core.JsonApplicationContext;
import xyz.dsvshx.ioc.entity.Robot;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class Test {
    public static void main(String[] args) throws Exception {
        JsonApplicationContext applicationContext = new JsonApplicationContext("application.json");
        applicationContext.init();
        Robot aiRobot = (Robot) applicationContext.getBean("robot");
        aiRobot.show();
    }
}
