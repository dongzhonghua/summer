package xyz.dsvshx.ioc.factory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.entity.BeanDefinition;
import xyz.dsvshx.ioc.entity.BeanReference;
import xyz.dsvshx.ioc.entity.ConstructorArg;
import xyz.dsvshx.ioc.entity.PropertyValue;
import xyz.dsvshx.ioc.util.BeanUtils;
import xyz.dsvshx.ioc.util.ClassUtils;
import xyz.dsvshx.ioc.util.ReflectionUtils;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
@Slf4j
public class AutowiredCapableBeanFactory extends AbstractBeanFactory {
    // 存放所有bean的
    private static final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 解决循环依赖
    private static final ConcurrentHashMap<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> getAllBean() throws Exception {
        return singletonObjects;
    }

    @Override
    Object doGetBean(BeanDefinition beanDefinition) throws Exception {
        //查找对象是否已经实例化过
        String beanName = beanDefinition.getId();
        Object bean = singletonObjects.get(beanName);
        if (beanDefinition.getIsSingleton() && bean != null) {
            return bean;
        }
        Object earlyBean = earlySingletonObjects.get(beanName);
        if (earlyBean != null) {
            log.info("循环依赖，提前返回尚未加载完成的bean:" + beanName);
            return earlyBean;
        }
        // 如果不存在则初始化
        bean = createBean(beanDefinition);
        if (bean != null) {
            // 然后如果B和A循环依赖，先把初始化成功的A放到一个map里，但是成员变量B还没有赋值。此时injectFields
            // 会初始化B，B会获取到A的引用，初始化完成之后返回B，然后A把B注入，完美解决了循环依赖的问题。
            earlySingletonObjects.put(beanName, bean);
            //对象创建成功以后，注入对象需要的参数
            populateBean(bean, beanDefinition);
            //再把对象存入Map中, 已经是一个完整的bean了。
            // prototype的情况也放到这里了，这种情况下有没有更好地处理方式？
            singletonObjects.put(beanName, bean);
            if (beanDefinition.getIsSingleton()) {
                beanDefinition.setBean(bean);
            }
            earlySingletonObjects.remove(beanName);
        }
        //结束返回
        return bean;
    }

    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        String beanName = beanDefinition.getBeanClassName();
        Class<?> clz = ClassUtils.loadClass(beanName);
        if (clz == null) {
            throw new Exception("can not find bean by beanName");
        }
        List<ConstructorArg> constructorArgs = beanDefinition.getConstructorArgs();
        if (constructorArgs != null && !constructorArgs.isEmpty()) {
            List<Object> objects = new ArrayList<>();
            for (ConstructorArg constructorArg : constructorArgs) {
                objects.add(getBean(constructorArg.getRef()));
            }
            return BeanUtils.getInstanceByCglib(clz, clz.getConstructor(), objects.toArray());
        } else {
            return BeanUtils.getInstanceByCglib(clz, null, null);
        }
    }

    private void populateBean(Object bean, BeanDefinition beanDefinition) throws Exception {
        List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (PropertyValue propertyValue : propertyValues) {
                Field declaredField = bean.getClass().getSuperclass().getDeclaredField(propertyValue.getName());
                Object fieldValue;
                Object value = propertyValue.getValue();
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    if (beanReference.getBean() != null) {
                        fieldValue = beanReference.getBean();
                    } else {
                        fieldValue = getBean(beanReference.getName());
                        beanReference.setBean(fieldValue);
                    }
                } else {
                    fieldValue = value;
                }
                if (fieldValue != null) {
                    ReflectionUtils.injectField(declaredField, bean, fieldValue);
                }
            }
        }
        // 这里如果是用代理对象的话则需要用getSuperclass,因为代理使用的继承，但是如果是用直接newInstance则不需要了，所以这里怎么做？
        // Field[] declaredFields = bean.getClass().getSuperclass().getDeclaredFields();
        // if (declaredFields != null) {
        // for (Field field : declaredFields) {
        //     String beanName = field.getName();
        //     beanName = StringUtils.uncapitalize(beanName);
        //     if (beanNameSet.contains(field.getName())) {
        //         Object fieldBean = getBean(beanName);
        //         if (fieldBean != null) {
        //             ReflectionUtils.injectField(field, bean, fieldBean);
        //         }
        //     } else {
        //
        //     }
        // }

        // }
    }
}
