package xyz.dsvshx.ioc.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-03-13
 */
@Data
@AllArgsConstructor
public class User {
    private String name;
    private String des;
    private Integer age;
}
