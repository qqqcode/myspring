package com.qqqspring.boot;


import com.qqqspring.beans.factory.BeanFactory;
import com.qqqspring.context.support.QqqAbstractApplicationContext;
import com.qqqspring.tools.BeanUtils;
import com.qqqspring.tools.ClassParseUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QqqSpringApplication {

    public QqqSpringApplication() {

    }

    public static QqqAbstractApplicationContext run(Class<?> source, String[] args) {
        return null;
    }

    private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
        ClassLoader classLoader = this.getClassLoader();
        List<T> instances = new ArrayList<>();
        BeanFactory beanFactory;
        Class<?> aClass = ClassParseUtil.forName("", classLoader);
        try {
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
            T o = (T) declaredConstructor.newInstance();
            instances.add(o);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return instances;
    }

    ClassLoader getClassLoader() {
        return ClassParseUtil.getDefaultClassLoader();
    }


    QqqAbstractApplicationContext run() {
        QqqAbstractApplicationContext context = createApplicationContext();
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

}
