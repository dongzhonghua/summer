package xyz.dsvshx.ioc.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import lombok.Data;
import xyz.dsvshx.ioc.bean.BeanDefinition;
import xyz.dsvshx.ioc.bean.ConstructorArg;
import xyz.dsvshx.ioc.util.BeanUtils;
import xyz.dsvshx.ioc.util.ClassUtils;
import xyz.dsvshx.ioc.util.ReflectionUtils;

/**
 * @author dongzhonghua
 * Created on 2021-03-09
 */
@Data
public class BeanFactoryImpl implements BeanFactory {
    // 存放所有bean的
    private static final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 解决循环依赖
    private static final ConcurrentHashMap<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    // 对象的名称和对象对应的数据结构的映射
    private static final ConcurrentHashMap<String, BeanDefinition> beanDefineMap = new ConcurrentHashMap<>();

    private static final Set<String> beanNameSet = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Object getBean(String beanName) throws Exception {
        //查找对象是否已经实例化过

        Object bean = singletonObjects.get(beanName);
        if (bean != null) {
            return bean;
        }
        Object earlyBean = earlySingletonObjects.get(beanName);
        if (earlyBean != null) {
            System.out.println("循环依赖，提前返回尚未加载完成的bean:" + beanName);
            return earlyBean;
        }
        // 如果不存在则初始化
        bean = createBean(beanDefineMap.get(beanName));
        if (bean != null) {
            // 然后如果B和A循环依赖，先把初始化成功的A放到一个map里，但是成员变量B还没有赋值。此时injectFields
            // 会初始化B，B会获取到A的引用，初始化完成之后返回B，然后A把B注入，完美解决了循环依赖的问题。
            earlySingletonObjects.put(beanName, bean);
            //对象创建成功以后，注入对象需要的参数
            populateBean(bean);
            //再把对象存入Map中方便下次使用。
            singletonObjects.put(beanName, bean);

            earlySingletonObjects.remove(beanName);
        }

        //结束返回
        return bean;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getSimpleName());
    }

    @Override
    public Map<String, Object> getAllBean() throws Exception {
        return singletonObjects;
    }

    protected void registerBean(String name, BeanDefinition bd) {
        beanDefineMap.put(name, bd);
        beanNameSet.add(name);
    }

    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        String beanName = beanDefinition.getClassName();
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

    private void populateBean(Object bean) throws Exception {
        // 这里如果是用代理对象的话则需要用getSuperclass,因为代理使用的继承，但是如果是用直接newInstance则不需要了，所以这里怎么做？
        Field[] declaredFields = bean.getClass().getSuperclass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                String beanName = field.getName();
                beanName = StringUtils.uncapitalize(beanName);
                if (beanNameSet.contains(field.getName())) {
                    Object fieldBean = getBean(beanName);
                    if (fieldBean != null) {
                        ReflectionUtils.injectField(field, bean, fieldBean);
                    }
                }
            }
        }
    }


    // 被代理过得对象如何获取原对象，目前这里还不成功
    public Object getTarget(Object proxy) throws Exception {
        Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
        field.setAccessible(true);
        //获取指定对象中此字段的值
        Object object = field.get(proxy);//获取Proxy对象中的此字段的值
        Field person = object.getClass().getDeclaredField("target");
        person.setAccessible(true);
        return person.get(proxy);
    }


    @Test
    public void inject() throws Exception {
        BeanFactoryImpl instanceByCglib = BeanUtils.getInstanceByCglib(BeanFactoryImpl.class, null, null);
        // System.out.println(Arrays.toString(instanceByCglib.getClass().getSuperclass().getDeclaredFields()));
        // System.out.println(getTarget(instanceByCglib));
    }
}
