package com.qqqspring;

public class BeanDefinition {
    private String scope;
    private Class beanClass;
    private String lazyInit;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(String lazyInit) {
        this.lazyInit = lazyInit;
    }
}
