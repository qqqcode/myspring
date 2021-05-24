package com.qqqspring.beans.factory;

import com.qqqspring.beans.BeansException;

/**
 * @author Johnson
 * 2021/5/24
 */
public class NoSuchBeanDefinitionException extends BeansException {

    private final String beanName;

    public NoSuchBeanDefinitionException(String msg) {
        super("No bean named '" + msg + "' available");
        this.beanName = msg;
    }

    public NoSuchBeanDefinitionException(String msg, Throwable cause) {
        super(msg, cause);
        this.beanName = msg;
    }
}
