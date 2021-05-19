package com.qqqspring;


import com.qqqspring.beans.annotation.AutoWired;
import com.qqqspring.context.annotation.ComponentScan;
import com.qqqspring.context.annotation.Scope;
import com.qqqspring.tools.Assert;
import com.qqqspring.tools.ClassParseUtil;
import com.qqqspring.tools.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = null;
    private ConcurrentHashMap<String, Object> singletonObjects = null;
    private List<BeanPostProcessor> beanPostProcessorList = null;

    public ApplicationContext(Class configClass) {
        ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String scanPath = annotation.value();
        System.out.println("开启扫描：扫描路径为" + scanPath);
        try {
            this.initBeans(scanPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            Class<?> aClass = Class.forName("com.qqq.service.UserService");
            System.out.println(aClass.getName());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getBean(String beanName) {
        Assert.hasText(beanName, beanName + "bean 名字不能为空");
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("prototype")) {
            return createBean(beanName, beanDefinition);
        } else {
            Object bean = singletonObjects.get(beanName);
            if (bean == null) {
                bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
            return bean;
        }
    }

    private void initBeans(String sacnPath) throws Exception {
        //使用java的反射机制扫包,[获取当前包下所有的类]
        System.out.println("扫描路径下所有的类");
        List<Class<?>> classes = ClassParseUtil.getClasses(sacnPath);
        //判断类上面是否存在注入的bean的注解
        System.out.println("开始初始化bean");
        ConcurrentHashMap<String, BeanDefinition> classExisAnnotation = findClassExisAnnotation(classes);
        System.out.println("初始化bean完成");
        if (classExisAnnotation.isEmpty()) {
            throw new Exception("该包下没有任何类加上注解");
        }
    }

    private ConcurrentHashMap<String, BeanDefinition> findClassExisAnnotation(List<Class<?>> classes) {
        if (classes.isEmpty()) {
            return null;
        }
        beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
        beanPostProcessorList = new ArrayList<BeanPostProcessor>();
        //遍历扫描的所有类，并识别所有有Component注释的类
        for (Class<?> aClass : classes) {
            if (aClass.isAnnotationPresent(Component.class)) {

                Component component = aClass.getAnnotation(Component.class);
                String beanName = component.value();
                if (StringUtils.isEmpty(beanName)) {
                    String[] split = aClass.getName().split(".");
                    beanName = split[split.length - 1];
                }

                synchronized (this.beanDefinitionMap) {

                    BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                    if (beanDefinition != null) {
                        throw new RuntimeException(beanName + "beanName重复定义");
                    }
                    beanDefinition = new BeanDefinition();
                    beanDefinition.setBeanClass(aClass);

                    if (aClass.isAnnotationPresent(Scope.class)) {
                        Scope scope = aClass.getAnnotation(Scope.class);
                        beanDefinition.setScope(scope.value());
                    } else {
                        beanDefinition.setScope("singleton");
                    }

                    if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                        try {
                            BeanPostProcessor bpp = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                            beanPostProcessorList.add(bpp);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }

                    beanDefinitionMap.put(beanName, beanDefinition);
                }
            }
        }
        System.out.println("初始化单例对象");
        initSingletonBeans(beanDefinitionMap);
        System.out.println("单例对象初始化完成");
        return beanDefinitionMap;
    }

    private void initSingletonBeans(ConcurrentHashMap<String, BeanDefinition> concurrentHashMap) {
        if (concurrentHashMap.isEmpty()) {
            return;
        }
        singletonObjects = new ConcurrentHashMap<String, Object>();
        for (String beanName : concurrentHashMap.keySet()) {
            BeanDefinition definition = concurrentHashMap.get(beanName);
            if (definition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, definition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    private Object createBean(String beanName, BeanDefinition definition) {
        Class beanClass = definition.getBeanClass();
        Object bean = null;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(AutoWired.class)) {
                    Object o = this.getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(bean, o);
                }
            }

            //Aware
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            }

            //init bean
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(bean, beanName);
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
        return bean;
    }

    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    protected Object getSingleton(String beanName, boolean allowEarlyRefrence) {
        Object o = singletonObjects.get(beanName);
        if (o == null) {
            synchronized (this.singletonObjects) {

            }
        }
        return (o == null ? null : o);
    }
}
