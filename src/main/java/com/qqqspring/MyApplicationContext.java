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
        beans = new ConcurrentHashMap<String, Object>();

    }

    public Object getBean(String beanName){
        return null;
    }

    private void initBeans(String sacnPath) throws Exception {
        // 1.使用java的反射机制扫包,[获取当前包下所有的类]
        List<Class<?>> classes = ClassParseUtil.getClasses(sacnPath);
        // 2、判断类上面是否存在注入的bean的注解
//        ConcurrentHashMap<String, Object> classExisAnnotation = findClassExisAnnotation(classes);
//        if (classExisAnnotation == null || classExisAnnotation.isEmpty()) {
//            throw new Exception("该包下没有任何类加上注解");
//        }

    }
}
