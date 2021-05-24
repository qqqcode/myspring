package com.qqqspring.beans.factory.config;

/**
 * @author Johnson
 * 2021/5/19
 */
public interface BeanDefinition {

    String getScope();

    void setScope(String scope);

    Class getBeanClass();

    void setBeanClass(Class<?> beanClass);

    void setBeanClassName(String var1);

    String getBeanClassName();

    void setLazyInit(boolean var1);

    boolean isLazyInit();

}
