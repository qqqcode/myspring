package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.config.BeanDefinition;

/**
 * @author Johnson
 * 2021/5/24
 */
public class GenericBeanDefinition extends AbstractBeanDefinition {

    protected GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

}
