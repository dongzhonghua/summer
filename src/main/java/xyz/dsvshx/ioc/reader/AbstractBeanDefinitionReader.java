package xyz.dsvshx.ioc.reader;


import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.io.ResourceLoader;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Data
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private Map<String, BeanDefinition> registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<>();
        this.resourceLoader = resourceLoader;
    }
}
