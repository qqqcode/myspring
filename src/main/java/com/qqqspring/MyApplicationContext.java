package com.qqqspring;


import com.qqqspring.tools.ClassParseUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = null;
    private ConcurrentHashMap<String,Object> singletonObjects = null;

    public MyApplicationContext(Class configClass){
        ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String scanPath = annotation.value();
        System.out.println(scanPath);
        try {
            this.initBeans(scanPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if(beanDefinition.getScope().equals("prototype")){
            return createBean(beanDefinition);
        }else {
            Object bean = singletonObjects.get(beanName);
            if(bean == null){
                bean = createBean(beanDefinition);
                singletonObjects.put(beanName,bean);
            }
            return bean;
        }
    }

    private void initBeans(String sacnPath) throws Exception {
        //使用java的反射机制扫包,[获取当前包下所有的类]
        List<Class<?>> classes = ClassParseUtil.getClasses(sacnPath);
        //判断类上面是否存在注入的bean的注解
        ConcurrentHashMap<String, BeanDefinition> classExisAnnotation = findClassExisAnnotation(classes);
        if (classExisAnnotation.isEmpty()) {
            throw new Exception("该包下没有任何类加上注解");
        }
    }

    private ConcurrentHashMap<String, BeanDefinition> findClassExisAnnotation(List<Class<?>> classes) throws Exception {
        if(classes.isEmpty()){
            throw new Exception("没有找到类");
        }
        ConcurrentHashMap<String,BeanDefinition> concurrentHashMap = new ConcurrentHashMap<String, BeanDefinition>();
        //遍历扫描的所有类，并识别所有有Component注释的类
        for (Class<?> aClass : classes) {
            if(aClass.isAnnotationPresent(Component.class)){
                Component component = aClass.getAnnotation(Component.class);
                Scope scope = aClass.getAnnotation(Scope.class);
                String beanName = component.value();

                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(aClass);
                beanDefinition.setScope(scope.value());

                concurrentHashMap.put(beanName,beanDefinition);
            }
        }
        initSingletonBeans(concurrentHashMap);
        return concurrentHashMap;
    }

    private void initSingletonBeans(ConcurrentHashMap<String,BeanDefinition> concurrentHashMap) {
        if(concurrentHashMap.isEmpty()){
            return;
        }
        singletonObjects = new ConcurrentHashMap<String, Object>();
        for (String beanName : concurrentHashMap.keySet()) {
            BeanDefinition definition = concurrentHashMap.get(beanName);
            if(definition.getScope().equals("singleton")){
                Object bean = createBean(definition);
                singletonObjects.put(beanName,bean);
            }
        }

    }

    private Object createBean(BeanDefinition definition) {
        Class beanClass = definition.getBeanClass();
        try {
            Object bean = beanClass.getDeclaredConstructor().newInstance();
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if(declaredField.isAnnotationPresent(AutoWired.class)){
                    Object o = this.getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(bean,o);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return beanClass;
    }
}
