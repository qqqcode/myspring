package com.qqqspring.boot;

import com.qqqspring.beans.factory.BeanFactory;
import com.qqqspring.context.support.AbstractApplicationContext;
import com.qqqspring.tools.ClassParseUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class QqqSpringApplication {


    public QqqSpringApplication(){

    }

    private <T> Collection<T> getSpringFactoriesInstances(Class<T> type){
        ClassLoader classLoader = this.getClassLoader();
        List<T> instances = new ArrayList<>();
        BeanFactory beanFactory;
        Class<?> aClass = ClassParseUtil.forName("", classLoader);
        try {
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
            T o = (T)declaredConstructor.newInstance();
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

    public static AbstractApplicationContext run(Class<?> source,String[] args){
        return null;
    }
}
