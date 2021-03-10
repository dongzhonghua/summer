package xyz.dsvshx.ioc.bean;

import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
public class ConstructorArg {
    private int index;
    private String ref;
    private String name;
    private Object value;
}
