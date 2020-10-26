package com.qqqspring;


import com.qqqspring.tools.ClassParseUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private ConcurrentHashMap<String, Object> beans = null;

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
        Object o = beans.get(beanName);
        return o;
    }

    private void initBeans(String sacnPath) throws Exception {
        // 1.使用java的反射机制扫包,[获取当前包下所有的类]
        List<Class<?>> classes = ClassParseUtil.getClasses(sacnPath);
        // 2、判断类上面是否存在注入的bean的注解
        ConcurrentHashMap<String, Object> classExisAnnotation = findClassExisAnnotation(classes);
        if (classExisAnnotation == null || classExisAnnotation.isEmpty()) {
            throw new Exception("该包下没有任何类加上注解");
        }
        beans = new ConcurrentHashMap<String, Object>();
        beans.putAll(classExisAnnotation);
    }

    private ConcurrentHashMap<String, Object> findClassExisAnnotation(List<Class<?>> classes) throws Exception {
        if(classes.isEmpty()||classes==null){
            throw new Exception("没有找到类");
        }
        ConcurrentHashMap<String,Object> concurrentHashMap = new ConcurrentHashMap<String, Object>();
        for (Class<?> aClass : classes) {
            Component annotation = aClass.getAnnotation(Component.class);
            if(annotation!=null){
                String name = annotation.value();
                Object o = aClass.newInstance();
                concurrentHashMap.put(name,o);
            }
        }
        return concurrentHashMap;
    }
}
