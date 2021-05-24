package com.qqqspring.context.support;

import com.qqqspring.beans.factory.support.AbstractBeanFactory;
import com.qqqspring.context.QqqApplicationContext;

/**
 * @author Johnson
 * 2021/5/19
 */
public abstract class QqqAbstractApplicationContext implements QqqApplicationContext {

    private final Object startupShutdownMonitor = new Object();


    @Override
    public Object getBean(String var1) {
        return null;
    }

    @Override
    public <T> T getBean(String var1, Class<T> var2) {
        return null;
    }

    @Override
    public Object getBean(String var1, Object... var2) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> var1) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> var1, Object... var2) {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    public abstract AbstractBeanFactory getBeanFactory();

    public void refresh() {
        synchronized (this.startupShutdownMonitor) {
            AbstractBeanFactory abstractBeanFactory = getBeanFactory();
            prepareBeanFactory(abstractBeanFactory);
        }
    }

    protected void prepareBeanFactory(AbstractBeanFactory beanFactory) {

    }

    protected void invokeBeanFactoryPostProcessors(AbstractBeanFactory beanFactory){
        
    }

}
