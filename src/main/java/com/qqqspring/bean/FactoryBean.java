package com.qqqspring.bean;

/**
 * @author Johnson
 * 2021/5/18
 */
public interface FactoryBean<T> {

    T getObject() throws Exception;

    Class<?> getObjectType();

    boolean isSingleton();

}
