package com.qqqspring.beans.factory;

import com.qqqspring.beans.BeansException;

/**
 * @author Johnson
 * 2021/5/24
 */
public class BeanInstantiationException extends BeansException {
    public BeanInstantiationException(String msg) {
        super(msg);
    }

    public BeanInstantiationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
