package xyz.dsvshx.ioc.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
@Builder
public class PropertyValue {

    private String name;

    private Object value;
}
