package com.qqqspring.beans;

/**
 * @author Johnson
 * 2021/5/24
 */
public abstract class BeansException extends RuntimeException {

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
