package com.qqq.service;

import com.qqqspring.BeanPostProcessor;
import com.qqqspring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("before");
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("after");
        return null;
    }
}
