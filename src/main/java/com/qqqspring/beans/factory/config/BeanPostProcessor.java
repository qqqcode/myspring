package com.qqqspring.beans.factory.config;

public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return null;
    }

    default Object postProcessAfterInitialization(Object bean,String beanName){
        return null;
    }
}
