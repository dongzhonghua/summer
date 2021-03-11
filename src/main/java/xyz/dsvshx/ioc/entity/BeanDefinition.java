package xyz.dsvshx.ioc.entity;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 从文件中读取的bean的定义信息
 *
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
@ToString
public class BeanDefinition {
    // 保存一份bean的定义
    private Object bean;

    private Class<?> beanClass;

    private String id;

    private String beanClassName;

    private Class<?>[] interfaces;

    private Boolean isSingleton;

    private List<ConstructorArg> constructorArgs;

    private List<PropertyValue> propertyValues;

}