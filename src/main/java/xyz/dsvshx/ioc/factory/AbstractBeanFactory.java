package xyz.dsvshx.ioc.factory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.entity.BeanDefinition;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
@Slf4j
public abstract class AbstractBeanFactory implements BeanFactory {

    // 对象的名称和对象对应的数据结构的映射
    private static final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    protected static final Set<String> beanNameSet = Collections.synchronizedSet(new HashSet<>());


    @Override
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        return doGetBean(beanDefinition);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        BeanDefinition beanDefinition = null;
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> tmpClass = entry.getValue().getBeanClass();
            if (tmpClass == beanClass || beanClass.isAssignableFrom(tmpClass)) {
                beanDefinition = entry.getValue();
            }
        }
        if (beanDefinition == null) {
            throw new RuntimeException("Unable to find the bean of this class, please check!");
        }
        return doGetBean(beanDefinition);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        beanDefinitionMap.put(name, bd);
        beanNameSet.add(name);
        log.info("注册 beanDefinition, bean name:{}, definition:{}", name, bd);
    }

    /**
     * 定义一个创建bean的抽象方法，由不同的实现类来实现
     */
    abstract Object doGetBean(BeanDefinition beanDefinition) throws Exception;

    public void populateBeans() throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            doGetBean(entry.getValue());
        }
    }

    @Override
    public abstract Map<String, Object> getAllBean() throws Exception;

}
