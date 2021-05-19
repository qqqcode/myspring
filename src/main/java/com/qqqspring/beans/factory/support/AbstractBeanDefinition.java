package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.config.BeanDefinition;

/**
 * @author Johnson
 * 2021/5/19
 */
public abstract class AbstractBeanDefinition implements BeanDefinition, Cloneable {
    private String scope;
    private Class beanClass;
    private String lazyInit;


}
