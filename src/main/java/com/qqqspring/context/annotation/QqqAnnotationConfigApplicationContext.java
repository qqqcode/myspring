package com.qqqspring.context.annotation;

import com.qqqspring.beans.factory.support.AbstractBeanFactory;
import com.qqqspring.beans.factory.support.DefaultBeanFactory;
import com.qqqspring.context.support.QqqAbstractApplicationContext;

/**
 * @author Johnson
 * 2021/5/24
 */
public class QqqAnnotationConfigApplicationContext extends QqqAbstractApplicationContext{

    private final DefaultBeanFactory beanFactory;

    public QqqAnnotationConfigApplicationContext() {
        this.beanFactory = new DefaultBeanFactory();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    @Override
    public AbstractBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
}
