package com.qqqspring;

import java.lang.annotation.Annotation;

public class MyApplicationContext {
    public MyApplicationContext(Class configClass){
        ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String sacnPath = annotation.value();
        System.out.println(sacnPath);

    }

    public Object getBean(String beanName){
        return null;
    }
}
