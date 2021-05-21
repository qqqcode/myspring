package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.config.BeanDefinition;

/**
 * @author Johnson
 * 2021/5/21
 */
public abstract class BeanDefinitionRegistry implements AliasRegistry {

    void registerBeanDefinition(String var1, BeanDefinition var2) {}

    void removeBeanDefinition(String var1) {}

    BeanDefinition getBeanDefinition(String var1) {
        BeanDefinition beanDefinition = null;
        return beanDefinition;
    }

    boolean containsBeanDefinition(String var1) {
        return false;
    }

    String[] getBeanDefinitionNames() {
        return new String[1];
    }

    int getBeanDefinitionCount() {
        return 0;
    }

    boolean isBeanNameInUse(String var1) {
        return false;
    }
}
