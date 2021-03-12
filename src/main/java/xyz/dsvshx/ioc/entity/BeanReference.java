package xyz.dsvshx.ioc.entity;

import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Data
public class BeanReference {
    public BeanReference(String name) {
        this.name = name;
    }
    // 小驼峰
    private String name;
    private Object bean;
}
