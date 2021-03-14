package xyz.dsvshx.ioc.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import xyz.dsvshx.ioc.annotation.Component;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RestController {
    String value() default "";
}
