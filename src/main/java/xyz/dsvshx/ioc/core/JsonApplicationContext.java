package xyz.dsvshx.ioc.core;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import xyz.dsvshx.ioc.bean.BeanDefinition;
import xyz.dsvshx.ioc.util.JsonUtils;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class JsonApplicationContext extends BeanFactoryImpl {

    private String fileName;

    public JsonApplicationContext(String fileName) {
        this.fileName = fileName;
    }

    public void init() {
        loadFile();
    }

    private void loadFile() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        List<BeanDefinition> beanDefinitions = JsonUtils.readValue(is, new TypeReference<List<BeanDefinition>>() {
        });
        if (beanDefinitions != null && !beanDefinitions.isEmpty()) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                registerBean(beanDefinition.getName(), beanDefinition);
            }
        }
    }
}
