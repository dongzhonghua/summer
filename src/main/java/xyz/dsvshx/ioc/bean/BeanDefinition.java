package xyz.dsvshx.ioc.bean;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
@ToString
public class BeanDefinition {

    private String name;

    private String className;

    private String interfaceName;

    private List<ConstructorArg> constructorArgs;

    private List<PropertyArg> propertyArgs;

}