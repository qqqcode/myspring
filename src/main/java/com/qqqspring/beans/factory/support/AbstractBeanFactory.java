package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.BeanFactory;
import com.qqqspring.beans.factory.config.BeanPostProcessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Johnson
 * 2021/5/21
 */
public abstract class AbstractBeanFactory extends BeanDefinitionRegistry implements BeanFactory {

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList();

    private final Map<String, AbstractBeanDefinition> beanDefinitions = new ConcurrentHashMap(256);

    public Object getBean(String name) {
        return this.doGetBean(name, (Class) null, (Object[]) null, false);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return this.doGetBean(name, requiredType, (Object[]) null, false);
    }

    public Object getBean(String name, Object... args) {
        return this.doGetBean(name, (Class) null, args, false);
    }

    public <T> T getBean(Class<T> requiredType) {
        return this.doGetBean(null, requiredType, null, false);
    }


    public <T> T getBean(Class<T> requiredType, Object... args) {
        return this.doGetBean(null, requiredType, args, false);
    }

    public <T> T getBean(String name, Class<T> requiredType, Object... args) {
        return this.doGetBean(name, requiredType, args, false);
    }

    public boolean containsBean(String name) {
        String canonicalName = name;

        String resolvedName;

        return false;
    }

    public boolean isSingleton(String name) {
        return false;
    }

    public boolean isPrototype(String name) {
        return false;
    }

    public boolean isTypeMatch(String name, Class<?> targetType) {
        return false;
    }

    public Class<?> getType(String name) {
        return null;
    }

    public String[] getAliases(String name) {
        return null;
    }

    protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) {
        Object bean = null;
        return (T) bean;
    }
}
