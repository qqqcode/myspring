package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.config.BeanDefinition;

import java.util.Objects;

/**
 * @author Johnson
 * 2021/5/19
 */
public abstract class AbstractBeanDefinition implements BeanDefinition, Cloneable {

    private String scope;

    private volatile Object beanClass;

    private Boolean lazyInit;

    protected AbstractBeanDefinition(BeanDefinition original) {
        this.scope = "";
        if (original instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition)original;
            Boolean lazyInit = originalAbd.isLazyInit();
            if (lazyInit != null) {
                this.setLazyInit(lazyInit);
            }
        } else {
            this.setLazyInit(original.isLazyInit());
        }
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit != null && this.lazyInit;
    }

    @Override
    public void setBeanClassName(String var1) {
        this.beanClass = var1;
    }

    @Override
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        return beanClassObject instanceof Class ? ((Class)beanClassObject).getName() : (String)beanClassObject;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        } else if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        } else {
            return (Class)beanClassObject;
        }
    }

    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }


    public abstract AbstractBeanDefinition cloneBeanDefinition();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBeanDefinition that = (AbstractBeanDefinition) o;
        return Objects.equals(scope, that.scope) &&
                Objects.equals(beanClass, that.beanClass) &&
                Objects.equals(lazyInit, that.lazyInit);
    }

}
