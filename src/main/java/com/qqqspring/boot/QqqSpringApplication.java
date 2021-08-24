package com.qqqspring.boot;


import com.qqqspring.context.annotation.ComponentScan;
import com.qqqspring.context.support.QqqAbstractApplicationContext;
import com.qqqspring.tools.BeanUtils;
import com.qqqspring.tools.ClassParseUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;


public class QqqSpringApplication {

    private Set<Class<?>> primarySources;

    public QqqSpringApplication() {

    }

    public QqqSpringApplication(Class<?> source) {
        this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));
        ComponentScan annotation = source.getAnnotation(ComponentScan.class);
        String scanPath = annotation.value();
        System.out.println("开启扫描：扫描路径为" + scanPath);
    }

    public static QqqAbstractApplicationContext run(Class<?> source, String[] args) {
        return new QqqSpringApplication(source).run();
    }

    ClassLoader getClassLoader() {
        return ClassParseUtil.getDefaultClassLoader();
    }


    QqqAbstractApplicationContext run() {
        QqqAbstractApplicationContext context = createApplicationContext();
        this.refresh(context);
        return context;
    }

    protected QqqAbstractApplicationContext createApplicationContext() {
        Class<?> aClass = null;
        try {
            aClass = Class.forName("com.qqqspring.context.annotation.QqqAnnotationConfigApplicationContext");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (QqqAbstractApplicationContext) BeanUtils.instantiateClass(aClass);
    }

    protected void refresh(QqqAbstractApplicationContext abstractApplicationContext) {
        abstractApplicationContext.refresh();
    }
}
