package com.qqq.service;

import com.qqqspring.beans.factory.config.BeanPostProcessor;
import com.qqqspring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println( beanName + "before");
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println( beanName + "after");
        return null;
    }
}
